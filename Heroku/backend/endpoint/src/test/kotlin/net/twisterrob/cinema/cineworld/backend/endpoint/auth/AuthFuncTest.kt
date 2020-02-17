package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
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
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.inject.Singleton

class AuthFuncTest {
	@Inject lateinit var mockRepository: AuthRepository

	@Test
	fun `successful Google OAuth 2 login`() {
		lateinit var state: String

		val mockClient = HttpClient(mockEngine())
		mockClient.stub { request ->
			when (request.url.toString()) {
				"https://oauth2.googleapis.com:443/token" -> {
					val textContent = request.body as TextContent
					assertEquals(ContentType.Application.FormUrlEncoded, textContent.contentType)
					assertEquals(
						"client_id=fake_google_client_id" +
								"&client_secret=fake_google_client_secret" +
								"&grant_type=authorization_code" +
								"&state=$state" +
								"&code=fake_code" +
								"&redirect_uri=http%3A%2F%2F127.0.0.1%2Fauth%2Fgoogle%2Freturn",
						textContent.text
					)

					respond(
						content = """{
                            "access_token": "fake_access_token",
                            "token_type": "fake_token_type",
                            "expires_in": 3600,
                            "refresh_token": "fake_refresh_token"
                        }""",
						headers = headersOf(
							HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
						)
					)
				}

				"https://openidconnect.googleapis.com/v1/userinfo" -> {
					assertEquals("Bearer fake_access_token", request.headers[HttpHeaders.Authorization])
					respond(
						content = """{
                            "sub": "fake_google_sub",
                            "email": "fake@google.email",
                            "email_verified": true,
                            "name": "Fake Google Name"
                        }""",
						headers = headersOf(
							HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
						)
					)
				}

				else -> error("Unhandled ${request.url}")
			}
		}

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
					createComponentBuilder = DaggerAuthFuncTestComponent::builder,
					initComponent = { builder ->
						builder
							.httpClient(mockClient)
							.users(mock())
					},
					componentReady = { (it as AuthFuncTestComponent).inject(this@AuthFuncTest) }
				)
			}
		) {
			handleRequest {
				method = HttpMethod.Get
				uri = "/auth/google/return"
				addHeader("Host", "127.0.0.1")
			}.apply {
				val location = response.headers["Location"] ?: ""
				assertEquals(
					"https://accounts.google.com/o/oauth2/auth" +
							"?client_id=fake_google_client_id" +
							"&redirect_uri=http%3A%2F%2F127.0.0.1%2Fauth%2Fgoogle%2Freturn" +
							"&scope=openid+email+profile" +
							"&state=****" +
							"&response_type=code",
					Regex("state=(\\w+)").replace(location, "state=****")
				)
				val stateInfo = Regex("state=(\\w+)").find(location)
				state = stateInfo!!.groupValues[1]
			}

			handleRequest {
				method = HttpMethod.Get
				uri = "/auth/google/return?state=$state&code=fake_code"
				addHeader("Host", "127.0.0.1")
			}.apply {
				assertEquals(HttpStatusCode.Found, response.status())
				assertEquals("/", response.headers[HttpHeaders.Location])
			}

			verify(mockRepository).addUser(
				userId = eq("fake_google_sub"),
				email = eq("fake@google.email"),
				name = eq("Fake Google Name"),
				realm = eq("http://127.0.0.1/"),
				created = any()
			)
		}
	}
}

@Component(
	modules = [
		Auth.FrontendModule::class
	]
)
@Singleton
private interface AuthFuncTestComponent : ApplicationComponent {

	fun inject(test: AuthFuncTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		@BindsInstance
		fun users(repository: AuthRepository): Builder

		override fun build(): AuthFuncTestComponent
	}
}
