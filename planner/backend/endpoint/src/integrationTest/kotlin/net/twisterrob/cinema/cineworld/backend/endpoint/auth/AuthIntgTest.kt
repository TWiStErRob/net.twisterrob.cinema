package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.flextrade.jfixture.JFixture
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.headersOf
import io.ktor.http.setCookie
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ClientProvider
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.AuthTestConstants.realisticUserId
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UnknownUserException
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.build
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import net.twisterrob.test.verify
import net.twisterrob.test.verifyNoInteractions
import net.twisterrob.test.verifyNoMoreInteractions
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.CheckReturnValue
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import javax.inject.Inject
import javax.inject.Singleton

class AuthIntgTest {

	@Inject lateinit var mockRepository: AuthRepository

	/**
	 * @see Auth.Routes.Google
	 * @see Auth.Routes.GoogleReturn
	 */
	@Test
	fun `successful Google OAuth 2 login`() {
		val fakeHost = "fake.host.name"
		val fakeAccessToken = "fake_access_token"
		val fakeRefreshToken = "fake_refresh_token"
		val fakeClientId = "fake_google_client_id"
		val fakeUserId = "fake_google_sub"
		val fakeRelativeUri = "/auth/google/return"
		val fakeClientSecret = "fake_google_client_secret"
		val fakeEmail = "fake@google.email"
		val fakeName = "Fake Google Name"

		val stubClient = HttpClient(mockEngine()) {
			followRedirects = false
		}
		endpointTest(
			configure = fakeClient(stubClient, fakeClientId, fakeClientSecret),
			daggerApp = createAppForAuthIntgTest(stubClient),
			externalServices = {
				hosts("https://accounts.google.com") {
					routing {
						get("/o/oauth2/auth") {
							call.respond(HttpStatusCode.OK, "")
						}
					}
				}
			}
		) {
			// Start the authorization flow.
			val state = authorizeWithGoogle(fakeHost, fakeRelativeUri, fakeClientId)
			stubClient.verifyNoInteractions()
			verifyNoInteractions(mockRepository)
			stubClient.stubGoogleToken(fakeAccessToken, fakeRefreshToken)
			stubClient.stubGoogleOpenIdUserInfo(fakeUserId, fakeEmail, fakeName)

			// Simulate the client receiving the authorization via redirect_uri from user interaction.
			val cookie = receiveAuthorizationFromGoogle(state, fakeHost, fakeRelativeUri)

			stubClient.verifyGoogleTokenRequest(fakeHost, fakeRelativeUri, state, fakeClientId, fakeClientSecret)
			stubClient.verifyGoogleOpenIdUserInfoRequest(fakeAccessToken)
			verify(mockRepository)
				.addUser(eq(fakeUserId), eq(fakeEmail), eq(fakeName), eq("http://${fakeHost}/"), any())
			assertThat(cookie, startsWith("userId=%23s${fakeUserId}/")) // %23 is # double-encoded.
			verifyNoMoreInteractions(mockRepository)
			stubClient.verifyNoMoreInteractions()
		}
	}

	/**
	 * @see Auth.Routes.Login
	 */
	@Test
	fun `login redirects to google`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		val response = noRedirectClient.get("/login")

		response.assertRedirect("/auth/google")
	}

	/**
	 * @see Auth.Routes.Logout
	 */
	@Test
	fun `logout clears auth cookie`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		whenever(mockRepository.findUser(realisticUserId)).thenReturn(JFixture().build())

		val response = noRedirectClient.get("/logout") { sendTestAuth() }

		assertThat(response.headers[HttpHeaders.SetCookie], startsWith("auth=; "))
		response.assertRedirect("/")
		verify(mockRepository).findUser(realisticUserId)
	}

	/**
	 * @see Auth.Routes.Google
	 */
	@Test
	fun `authorizing already logged in session with Google redirects to home`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		whenever(mockRepository.findUser(realisticUserId)).thenReturn(JFixture().build())

		val response = noRedirectClient.get("/auth/google") { sendTestAuth() }

		response.assertRedirect("/")
		verify(mockRepository).findUser(realisticUserId)
	}

	/**
	 * @see Auth.Routes.Account
	 */
	@Test
	fun `account page shows no user without session cookie`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		val response = client.get("/account")

		assertEquals("no user", response.bodyAsText())
	}

	/**
	 * @see Auth.Routes.Account
	 */
	@Test
	fun `account page shows error when invalid user in session`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		whenever(mockRepository.findUser(realisticUserId)).thenThrow(UnknownUserException("fake error"))

		val response = client.get("/account") {
			sendTestAuth()
		}

		assertThat(response.headers[HttpHeaders.SetCookie], startsWith("auth=; "))
		assertEquals("no user", response.bodyAsText())
		verify(mockRepository).findUser(realisticUserId)
	}

	/**
	 * @see Auth.Routes.Account
	 */
	@Test
	fun `account page shows user data with session cookie`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		val user: User = JFixture().build()
		whenever(mockRepository.findUser(realisticUserId)).thenReturn(user)

		val response = client.get("/account") { sendTestAuth() }

		JSONAssert.assertEquals(
			"""
				{
				  "id" : "${user.id}",
				  "email" : "${user.email}"
				}
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
		verify(mockRepository).findUser(realisticUserId)
	}

	/**
	 * TODO it should redirect to Google.
	 * @see Auth.Routes.Google
	 */
	@Test
	fun `authorizing new session with Google redirects to google-return`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		val response = noRedirectClient.get("/auth/google")

		response.assertRedirect("/auth/google/return")
	}

	private fun createAppForAuthIntgTest(
		stubClient: HttpClient = HttpClient(mockEngine())
	): Application.() -> Unit = {
		daggerApplication(
			createComponentBuilder = DaggerAuthIntgTestComponent::builder,
			initComponent = { builder ->
				builder
					.httpClient(stubClient)
					.users(mock())
			},
			componentReady = { (it as AuthIntgTestComponent).inject(this@AuthIntgTest) }
		)
	}
}

@Component(
	modules = [
		Auth.FrontendModule::class
	]
)
@Singleton
private interface AuthIntgTestComponent : ApplicationComponent {

	fun inject(test: AuthIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		@BindsInstance
		fun users(repository: AuthRepository): Builder

		override fun build(): AuthIntgTestComponent
	}
}

private val ClientProvider.noRedirectClient: HttpClient
	get() = this.createClient {
		followRedirects = false
	}

private fun fakeClient(
	stubClient: HttpClient,
	@Suppress("SameParameterValue") fakeClientId: String,
	@Suppress("SameParameterValue") fakeClientSecret: String
): Application.() -> Unit = {
	configuration(
		oauthHttpClient = stubClient,
		config = mapOf(
			"GOOGLE_CLIENT_ID" to fakeClientId,
			"GOOGLE_CLIENT_SECRET" to fakeClientSecret
		)
	)
}

private suspend fun ClientProvider.authorizeWithGoogle(host: String, relativeUri: String, clientId: String): String {
	val response = client.get(relativeUri) {
		header(HttpHeaders.Host, host)
	}

	val location = response.call.request.url
	val cleanLocation = URLBuilder(location).apply { parameters["state"] = "____" }.build()
	assertEquals(
		"https://accounts.google.com/o/oauth2/auth" +
				"?client_id=${clientId}" +
				"&redirect_uri=http%3A%2F%2F${host}${relativeUri.replace("/", "%2F")}" +
				"&scope=openid+email+profile" +
				"&state=____" +
				"&response_type=code",
		cleanLocation.toString()
	)
	response.assertStatus(HttpStatusCode.OK)
	return location.parameters["state"]!!
}

@CheckReturnValue
private suspend fun ClientProvider.receiveAuthorizationFromGoogle(
	state: String,
	host: String,
	relativeUri: String
): String {
	val response = noRedirectClient.get {
		url("${relativeUri}?state=${state}&code=fake_code")
		header(HttpHeaders.Host, host)
	}

	response.assertRedirect("/")
	return response.setCookie().single().value
}

private fun HttpClient.stubGoogleToken(accessToken: String, refreshToken: String) {
	stub("https://oauth2.googleapis.com/token") {
		respond(
			//language=JSON
			content = """
				{
				    "access_token": "${accessToken}",
				    "token_type": "fake_token_type",
				    "expires_in": 3600,
				    "refresh_token": "${refreshToken}"
				}
			""".trimIndent(),
			headers = headersOf(
				HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
			)
		)
	}
}

private fun HttpClient.verifyGoogleTokenRequest(
	host: String, relativeUri: String, state: String, clientId: String, clientSecret: String
) {
	verify("https://oauth2.googleapis.com/token") { request, _ ->
		val textContent = request.body as TextContent
		assertEquals(ContentType.Application.FormUrlEncoded, textContent.contentType)
		assertEquals(
			"client_id=${clientId}" +
					"&client_secret=${clientSecret}" +
					"&grant_type=authorization_code" +
					"&state=${state}" +
					"&code=fake_code" +
					"&redirect_uri=http%3A%2F%2F${host}${relativeUri.replace("/", "%2F")}",
			textContent.text
		)
	}
}

private fun HttpClient.stubGoogleOpenIdUserInfo(userId: String, email: String, name: String) {
	stub("https://openidconnect.googleapis.com/v1/userinfo") {
		respond(
			//language=JSON
			content = """
				{
				    "sub": "${userId}",
				    "email": "${email}",
				    "email_verified": true,
				    "name": "${name}"
				}
			""".trimIndent(),
			headers = headersOf(
				HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
			)
		)
	}
}

@Suppress("FunctionMaxLength")
private fun HttpClient.verifyGoogleOpenIdUserInfoRequest(accessToken: String) {
	verify("https://openidconnect.googleapis.com/v1/userinfo") { request, _ ->
		assertEquals("Bearer ${accessToken}", request.headers[HttpHeaders.Authorization])
	}
}

private fun HttpResponse.assertRedirect(url: String) {
	assertStatus(HttpStatusCode.Found)
	assertEquals(url, headers[HttpHeaders.Location])
}

private fun HttpResponse.assertStatus(statusCode: HttpStatusCode) {
	assertEquals(statusCode, status)
}

