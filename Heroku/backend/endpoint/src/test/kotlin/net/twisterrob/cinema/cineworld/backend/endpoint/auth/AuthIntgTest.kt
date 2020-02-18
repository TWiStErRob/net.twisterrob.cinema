@file:Suppress("RemoveCurlyBracesFromTemplate")

package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
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
import io.ktor.server.testing.TestApplicationEngine
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import net.twisterrob.test.verify
import net.twisterrob.test.verifyNoMoreInteractions
import net.twisterrob.test.verifyZeroInteractions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.inject.Singleton

@TagIntegration
class AuthIntgTest {

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

			receiveAuthorizationFromGoogle(state, fakeHost, fakeRelativeUri)

			stubClient.verifyGoogleTokenRequest(fakeHost, fakeRelativeUri, state, fakeClientId, fakeClientSecret)
			stubClient.verifyGoogleOpenIdUserInfoRequest(fakeAccessToken)
			verify(mockRepository)
				.addUser(eq(fakeUserId), eq(fakeEmail), eq(fakeName), eq("http://${fakeHost}/"), any())
			verifyNoMoreInteractions(mockRepository)
			stubClient.verifyNoMoreInteractions()
		}
	}

	private fun createAppForAuthIntgTest(
		stubClient: HttpClient
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
	fakeClientId: String,
	fakeClientSecret: String
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
		addHeader("Host", host)
	}.apply {
		val location = response.headers[HttpHeaders.Location] ?: ""
		assertEquals(
			"https://accounts.google.com/o/oauth2/auth" +
					"?client_id=${clientId}" +
					"&redirect_uri=http%3A%2F%2F${host}${relativeUri.replace("/", "%2F")}" +
					"&scope=openid+email+profile" +
					"&state=****" +
					"&response_type=code",
			Regex("state=(\\w+)").replace(location, "state=****")
		)
		val stateInfo = Regex("state=(?<state>\\w+)").find(location)
		nonce = stateInfo!!.groups["state"]!!.value
		assertEquals(HttpStatusCode.Found, response.status())
	}
	return nonce
}

private fun TestApplicationEngine.receiveAuthorizationFromGoogle(state: String, host: String, relativeUri: String) {
	handleRequest {
		method = HttpMethod.Get
		uri = "${relativeUri}?state=${state}&code=fake_code"
		addHeader("Host", host)
	}.apply {
		assertEquals(HttpStatusCode.Found, response.status())
		assertEquals("/", response.headers[HttpHeaders.Location])
	}
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
