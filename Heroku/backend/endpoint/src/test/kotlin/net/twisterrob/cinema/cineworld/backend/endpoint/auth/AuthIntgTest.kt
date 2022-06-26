@file:Suppress("RemoveCurlyBracesFromTemplate")

package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.flextrade.jfixture.JFixture
import dagger.BindsInstance
import dagger.Component
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UnknownUserException
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import net.twisterrob.test.verify
import net.twisterrob.test.verifyNoMoreInteractions
import net.twisterrob.test.verifyZeroInteractions
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.CheckReturnValue
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import javax.inject.Inject
import javax.inject.Singleton

@TagIntegration
class AuthIntgTest {

	companion object {

		/**
		 * It needs to be a cookie that uses the right secretSignKey in Sessions Ktor feature.
		 * If this is broken, debug a test where [receiveAuthorizationFromGoogle] is visible and capture the value.
		 * The rest of the [HttpHeaders.SetCookie] is omitted as it's not relevant here.
		 *
		 * @see configuration
		 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
		 */
		const val realisticCookie =
			"auth=userId%3D%2523srealistic%5Fgoogle%5Fsub%2F1d9f41780441596d9ec55c20219873d813180e8d1d7caab07e1463fcb6462622"

		/**
		 * User ID contained within [realisticCookie]. Needed to ensure session data is passed through the right way.
		 */
		const val realisticUserId = "realistic_google_sub"
	}

	@Inject lateinit var mockRepository: AuthRepository

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

		val stubClient = HttpClient(mockEngine())
		endpointTest(
			configure = fakeClient(stubClient, fakeClientId, fakeClientSecret),
			daggerApp = createAppForAuthIntgTest(stubClient)
		) {
			val state = authorizeWithGoogle(fakeHost, fakeRelativeUri, fakeClientId)
			stubClient.verifyZeroInteractions()
			verifyZeroInteractions(mockRepository)
			stubClient.stubGoogleToken(fakeAccessToken, fakeRefreshToken)
			stubClient.stubGoogleOpenIdUserInfo(fakeUserId, fakeEmail, fakeName)

			val cookie = receiveAuthorizationFromGoogle(state, fakeHost, fakeRelativeUri)

			stubClient.verifyGoogleTokenRequest(fakeHost, fakeRelativeUri, state, fakeClientId, fakeClientSecret)
			stubClient.verifyGoogleOpenIdUserInfoRequest(fakeAccessToken)
			verify(mockRepository)
				.addUser(eq(fakeUserId), eq(fakeEmail), eq(fakeName), eq("http://${fakeHost}/"), any())
			assertThat(cookie, startsWith("auth=userId%3D%2523s${fakeUserId.replace("_", "%5F")}%2F"))
			verifyNoMoreInteractions(mockRepository)
			stubClient.verifyNoMoreInteractions()
		}
	}

	@Test
	fun `logout clears auth cookie`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		whenever(mockRepository.findUser(realisticUserId)).thenReturn(JFixture().build())
		handleRequest {
			method = HttpMethod.Get
			uri = "/logout"
			addHeader(HttpHeaders.Cookie, realisticCookie)
		}.apply {
			assertThat(response.headers[HttpHeaders.SetCookie], startsWith("auth=; "))
			assertRedirect("/")
		}
		verify(mockRepository).findUser(realisticUserId)
	}

	@Test
	fun `authorizing already logged in session with Google redirects to home`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		whenever(mockRepository.findUser(realisticUserId)).thenReturn(JFixture().build())
		handleRequest {
			method = HttpMethod.Get
			uri = "/auth/google"
			addHeader(HttpHeaders.Cookie, realisticCookie)
		}.apply {
			assertRedirect("/")
		}
		verify(mockRepository).findUser(realisticUserId)
	}

	@Test
	fun `account page shows no user without session cookie`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		handleRequest {
			method = HttpMethod.Get
			uri = "/account"
		}.apply {
			assertEquals("no user", response.content)
		}
	}

	@Test
	fun `account page shows error when invalid user in session`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		whenever(mockRepository.findUser(realisticUserId)).thenThrow(UnknownUserException("fake error"))
		handleRequest {
			method = HttpMethod.Get
			uri = "/account"
			addHeader(HttpHeaders.Cookie, realisticCookie)
		}.apply {
			assertThat(response.headers[HttpHeaders.SetCookie], startsWith("auth=; "))
			assertEquals("no user", response.content)
		}
		verify(mockRepository).findUser(realisticUserId)
	}

	@Test
	fun `account page shows user data with session cookie`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		val user: User = JFixture().build()
		whenever(mockRepository.findUser(realisticUserId)).thenReturn(user)
		handleRequest {
			method = HttpMethod.Get
			uri = "/account"
			addHeader(HttpHeaders.Cookie, realisticCookie)
		}.apply {
			JSONAssert.assertEquals(
				"""
					{
					  "id" : "${user.id}",
					  "email" : "${user.email}"
					}
				""",
				response.content,
				JSONCompareMode.STRICT
			)
		}
		verify(mockRepository).findUser(realisticUserId)
	}

	/**
	 * TODO it should redirect to Google
	 */
	@Test
	fun `authorizing new session with Google redirects to google-return`() = endpointTest(
		daggerApp = createAppForAuthIntgTest()
	) {
		handleRequest {
			method = HttpMethod.Get
			uri = "/auth/google"
		}.apply {
			assertRedirect("/auth/google/return")
		}
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

private fun TestApplicationEngine.authorizeWithGoogle(host: String, relativeUri: String, clientId: String): String {
	val nonce: String
	handleRequest {
		method = HttpMethod.Get
		uri = relativeUri
		addHeader(HttpHeaders.Host, host)
	}.apply {
		val location = response.headers[HttpHeaders.Location] ?: ""
		@Suppress("HttpUrlsUsage") // This is a test server running on localhost.
		assertEquals(
			"https://accounts.google.com/o/oauth2/auth" +
					"?client_id=${clientId}" +
					// Verify unencoded for now. https://youtrack.jetbrains.com/issue/KTOR-2938
					"&redirect_uri=http://${host}${relativeUri}" +
					//"&redirect_uri=http%3A%2F%2F${host}${relativeUri.replace("/", "%2F")}" +
					"&scope=openid+email+profile" +
					"&state=****" +
					"&response_type=code",
			Regex("state=(\\w+)").replace(location, "state=****")
		)
		val stateInfo = Regex("state=(?<state>\\w+)").find(location)
		nonce = stateInfo!!.groups["state"]!!.value
		assertStatus(HttpStatusCode.Found)
	}
	return nonce
}

@CheckReturnValue
private fun TestApplicationEngine.receiveAuthorizationFromGoogle(
	state: String,
	host: String,
	relativeUri: String
): String {
	val cookie: String
	handleRequest {
		method = HttpMethod.Get
		uri = "${relativeUri}?state=${state}&code=fake_code"
		addHeader(HttpHeaders.Host, host)
	}.apply {
		assertRedirect("/")
		val setCookie = response.headers[HttpHeaders.SetCookie]!!
		val cookieDetails = Regex("(?<value>[^;]+);.*").find(setCookie)
		cookie = cookieDetails!!.groups["value"]!!.value
	}
	return cookie
}

private fun HttpClient.stubGoogleToken(accessToken: String, refreshToken: String) {
	stub("https://oauth2.googleapis.com:443/token") {
		respond(
			//language=JSON
			content = """
				{
				    "access_token": "${accessToken}",
				    "token_type": "fake_token_type",
				    "expires_in": 3600,
				    "refresh_token": "${refreshToken}"
				}
			""",
			headers = headersOf(
				HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
			)
		)
	}
}

private fun HttpClient.verifyGoogleTokenRequest(
	host: String, relativeUri: String, state: String, clientId: String, clientSecret: String
) {
	verify("https://oauth2.googleapis.com:443/token") { request, _ ->
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
			""",
			headers = headersOf(
				HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
			)
		)
	}
}

private fun HttpClient.verifyGoogleOpenIdUserInfoRequest(accessToken: String) {
	verify("https://openidconnect.googleapis.com/v1/userinfo") { request, _ ->
		assertEquals("Bearer ${accessToken}", request.headers[HttpHeaders.Authorization])
	}
}

private fun TestApplicationCall.assertRedirect(url: String) {
	assertStatus(HttpStatusCode.Found)
	assertEquals(url, response.headers[HttpHeaders.Location])
}

private fun TestApplicationCall.assertStatus(statusCode: HttpStatusCode) {
	assertEquals(statusCode, response.status())
}

