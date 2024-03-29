package net.twisterrob.cinema.cineworld.backend.endpoint.film

import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.hasUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.userId
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.cached
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Films
 */
@Singleton
class FilmsController @Inject constructor(
	application: Application,
	private val repository: FilmRepository
) : RouteController(application) {

	/**
	 * @see Films.Routes
	 */
	@Suppress("LabeledExpression") // https://github.com/detekt/detekt/issues/5132
	override fun Routing.registerRoutes() {

		get<Films.Routes.ListFilms> { listFilms ->
			if (call.hasUser) {
				// Note: this is not cache-able because of `Film.view` property in the response.
				call.respond(repository.getFilmsAuth(call.userId, listFilms.date, listFilms.cinemaIDs))
			} else {
				cached { call.respond(repository.getFilms(listFilms.date, listFilms.cinemaIDs)) }
			}
		}

		get<Films.Routes.GetFilm> { getFilm ->
			val film = repository.getFilm(getFilm.edi)
				?: return@get call.respondText("Film with EDI #${getFilm.edi} is not found.", status = NotFound)
			call.respond(film)
		}
	}
}
