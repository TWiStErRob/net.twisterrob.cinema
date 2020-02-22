package net.twisterrob.cinema.cineworld.backend.endpoint.film

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The [Films] controller. This controller handles the routes related to films.
 * It inherits [RouteController] that offers some basic functionality.
 */
@Singleton
class FilmsController @Inject constructor(
	application: Application,
	private val repository: FilmRepository
) : RouteController(application) {

	/**
	 * Registers the routes related to [Films].
	 */
	override fun Routing.registerRoutes() {

		get<Films.Routes.ListFilms> {
			TODO("implement based on index.js")
		}

		get<Films.Routes.GetFilm> { getFilm ->
			val film = repository.getFilm(getFilm.edi)
				?: return@get call.respondText("Film with EDI #${getFilm.edi} is not found.", status = NotFound)
			call.respond(film)
		}
	}
}
