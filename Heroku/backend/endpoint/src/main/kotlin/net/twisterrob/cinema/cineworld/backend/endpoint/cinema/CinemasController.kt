package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.locations.delete
import io.ktor.server.locations.get
import io.ktor.server.locations.put
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.hasUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.userId
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.cached
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Cinemas
 */
@Singleton
class CinemasController @Inject constructor(
	application: Application,
	private val repository: CinemaRepository
) : RouteController(application) {

	/**
	 * @see Cinemas.Routes
	 */
	override fun Routing.registerRoutes() {

		get<Cinemas.Routes.ListCinemas> {
			if (call.hasUser) {
				// Note: this is not cache-able because of `Cinema.fav` property in the response.
				call.respond(repository.getCinemasAuth(userID = call.userId))
			} else {
				cached { call.respond(repository.getActiveCinemas()) }
			}
		}

		get<Cinemas.Routes.ListFavoriteCinemas> {
			if (call.hasUser) {
				call.respond(repository.getFavoriteCinemas(userID = call.userId))
			} else {
				call.respondText("Can't find user.", status = NotFound)
			}
		}

		put<Cinemas.Routes.AddFavorite> { cinema ->
			if (call.hasUser) {
				val updatedCinema = repository.addFavorite(userID = call.userId, cinema = cinema.cinema)
					?: return@put call.respondText("Can't find cinema ${cinema.cinema}.", status = NotFound)
				call.respond(updatedCinema)
			} else {
				call.respondText("Can't find user.", status = NotFound)
			}
		}

		delete<Cinemas.Routes.RemoveFavorite> { cinema ->
			if (call.hasUser) {
				val updatedCinema = repository.removeFavorite(userID = call.userId, cinema = cinema.cinema)
					?: return@delete call.respondText("Can't find cinema ${cinema.cinema}.", status = NotFound)
				call.respond(updatedCinema)
			} else {
				call.respondText("Can't find user.", status = NotFound)
			}
		}
	}
}
