package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
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
		val mockClient = HttpClient(mockEngine())

		endpointTest(
			configure = {
				configuration(
					oauthHttpClient = mockClient,
					config = mapOf(
						"GOOGLE_CLIENT_ID" to "fake_google_client_id",
						"GOOGLE_CLIENT_SECRET" to "fake_google_client_secret"
					)
				)
			},
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerAuthIntgTestComponent::builder,
					initComponent = { builder ->
						builder
							.httpClient(mockClient)
							.users(mock())
					},
					componentReady = { (it as AuthIntgTestComponent).inject(this@AuthIntgTest) }
				)
			}
		) {
			lateinit var state: String

			handleRequest {
				method = HttpMethod.Get
				uri = "/auth/google/return"
				addHeader("Host", "fake.host.name")
			}.apply {
				val location = response.headers[HttpHeaders.Location] ?: ""
				assertEquals(
					"https://accounts.google.com/o/oauth2/auth" +
							"?client_id=fake_google_client_id" +
							"&redirect_uri=http%3A%2F%2Ffake.host.name%2Fauth%2Fgoogle%2Freturn" +
							"&scope=openid+email+profile" +
							"&state=****" +
							"&response_type=code",
					Regex("state=(\\w+)").replace(location, "state=****")
				)
				val stateInfo = Regex("state=(\\w+)").find(location)
				state = stateInfo!!.groupValues[1]
				assertEquals(HttpStatusCode.Found, response.status())
			}

			mockClient.verifyZeroInteractions()
			verifyZeroInteractions(mockRepository)

			mockClient.stub("https://oauth2.googleapis.com:443/token") {
				respond(
					//language=JSON
					content = """
						{
						    "access_token": "fake_access_token",
						    "token_type": "fake_token_type",
						    "expires_in": 3600,
						    "refresh_token": "fake_refresh_token"
						}
					""",
					headers = headersOf(
						HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
					)
				)
			}

			mockClient.stub("https://openidconnect.googleapis.com/v1/userinfo") {
				respond(
					//language=JSON
					content = """
						{
						    "sub": "fake_google_sub",
						    "email": "fake@google.email",
						    "email_verified": true,
						    "name": "Fake Google Name"
						}
					""",
					headers = headersOf(
						HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
					)
				)
			}

			handleRequest {
				method = HttpMethod.Get
				uri = "/auth/google/return?state=$state&code=fake_code"
				addHeader("Host", "fake.host.name")
			}.apply {
				assertEquals(HttpStatusCode.Found, response.status())
				assertEquals("/", response.headers[HttpHeaders.Location])
			}

			mockClient.verify("https://oauth2.googleapis.com:443/token") { request, _ ->
				val textContent = request.body as TextContent
				assertEquals(ContentType.Application.FormUrlEncoded, textContent.contentType)
				assertEquals(
					"client_id=fake_google_client_id" +
							"&client_secret=fake_google_client_secret" +
							"&grant_type=authorization_code" +
							"&state=$state" +
							"&code=fake_code" +
							"&redirect_uri=http%3A%2F%2Ffake.host.name%2Fauth%2Fgoogle%2Freturn",
					textContent.text
				)
			}

			mockClient.verify("https://openidconnect.googleapis.com/v1/userinfo") { request, _ ->
				assertEquals("Bearer fake_access_token", request.headers[HttpHeaders.Authorization])
			}

			verify(mockRepository).addUser(
				userId = eq("fake_google_sub"),
				email = eq("fake@google.email"),
				name = eq("Fake Google Name"),
				realm = eq("http://fake.host.name/"),
				created = any()
			)

			verifyNoMoreInteractions(mockRepository)
			mockClient.verifyNoMoreInteractions()
		}
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
