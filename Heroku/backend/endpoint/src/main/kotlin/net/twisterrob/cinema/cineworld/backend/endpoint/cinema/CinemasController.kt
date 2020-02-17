package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.isAuthenticated
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The Users controller. This controller handles the routes related to users.
 * It inherits [RouteController] that offers some basic functionality.
 */
@Singleton
class CinemasController @Inject constructor(
	application: Application,
	private val repository: CinemaRepository
) : RouteController(application) {

	/**
	 * Registers the routes related to [Cinemas].
	 */
	override fun Routing.registerRoutes() {

		get<Cinemas.Routes.ListCinemas> {
			if (call.isAuthenticated()) {
				call.respond(repository.getCinemasAuth(userID = call.userId))
			} else {
				call.respond(repository.getActiveCinemas())
			}
		}

		get<Cinemas.Routes.ListFavoriteCinemas> {
			TODO("Must be authenticated, don't know how to check yet")
			@Suppress("UNREACHABLE_CODE")
			call.respond(repository.getFavoriteCinemas(userID = call.userId))
		}
	}
}

/**
 * TODO stub to make it compile
 */
private val ApplicationCall.userId: Long get() = 0
