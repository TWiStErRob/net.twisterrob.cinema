package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.CurrentUser
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.currentUser
import net.twisterrob.cinema.cineworld.backend.endpoint.app.App
import net.twisterrob.cinema.cineworld.backend.endpoint.app.AppController
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UnknownUserException
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserInfoOpenID
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.absoluteUrl
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * See [Docs](https://developers.google.com/identity/protocols/OpenIDConnect).
 * See [Endpoint Discovery](https://accounts.google.com/.well-known/openid-configuration)
 * @see Auth
 */
@Singleton
class AuthController @Inject constructor(
	application: Application,
	httpClient: HttpClient,
	private val authRepository: AuthRepository
) : RouteController(application) {

	private val httpClient = httpClient.config {
		// to handle UserInfo
		install(JsonFeature) {
			serializer = JacksonSerializer()
		}
	}

	/**
	 * 1 below [AppController.order].
	 */
	@Suppress("MagicNumber") // TODO find a better mechanism for ordering, e.g. topological sort.
	override val order: Int get() = -2

	/**
	 * @see Auth.Routes
	 */
	@Suppress("LongMethod") // It's a collection of small methods without shared scope.
	override fun Routing.registerRoutes() {
		intercept(ApplicationCallPipeline.Features) {
			val session: AuthSession? = call.sessions.get()
			if (session != null) {
				try {
					val user = authRepository.findUser(session.userId)
					call.attributes.currentUser = CurrentUser(user.id, user.email)
				} catch (ex: UnknownUserException) {
					call.application.log.error("Invalid session: {}", call.sessions, ex)
					call.sessions.clear<AuthSession>()
				}
			}
		}

		authenticate(optional = true) {
			get<Auth.Routes.Account> {
				val currentUser = call.attributes.currentUser
				call.respond(currentUser ?: "no user")
			}
		}

		get<Auth.Routes.Login> {
			// TODO how to do internal redirect?
			call.respondRedirect(Auth.Routes.Google.href)
		}

		authenticate(optional = true) {
			get<Auth.Routes.Logout> {
				if (call.hasUser) {
					val currentUser = call.attributes.currentUser!!
					call.application.log.trace("Logging out {}.", currentUser)
					call.sessions.clear<AuthSession>()
					call.respondRedirect(App.Routes.Home.href)
				} else {
					call.application.log.warn("Already logged out.")
					call.respondRedirect(App.Routes.Home.href)
				}
			}
		}

		authenticate(optional = true) {
			get<Auth.Routes.Google> {
				if (call.hasUser) {
					val currentUser = call.attributes.currentUser!!
					call.application.log.trace("Already logged in as {}.", currentUser)
					call.respondRedirect(App.Routes.Home.href)
				} else {
					call.respondRedirect(Auth.Routes.GoogleReturn.href)
				}
			}
		}

		authenticate {
			get<Auth.Routes.GoogleReturn> {
				val principal: OAuthAccessTokenResponse.OAuth2 = call.authentication.principal()
					?: error("No principal, authentication failed?")

				val data: UserInfoOpenID = httpClient.get(UserInfoOpenID.URL.toString()) {
					header("Authorization", "Bearer ${principal.accessToken}")
				}
				authRepository.addUser(
					userId = data.sub,
					email = data.email!!,
					name = data.name ?: "Anonymous",
					realm = call.absoluteUrl(),
					created = OffsetDateTime.now()
				)

				call.sessions.set(
					AuthSession(userId = data.sub)
				)
				call.respondRedirect(App.Routes.Home.href)
			}
		}
	}
}
