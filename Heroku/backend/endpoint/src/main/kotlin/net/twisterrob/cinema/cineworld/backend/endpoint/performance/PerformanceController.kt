package net.twisterrob.cinema.cineworld.backend.endpoint.performance

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.PerformanceRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.cached
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Performances
 */
@Singleton
class PerformanceController @Inject constructor(
	application: Application,
	private val repository: PerformanceRepository
) : RouteController(application) {

	/**
	 * @see Performances.Routes
	 */
	override fun Routing.registerRoutes() {

		get<Performances.Routes.ListPerformances> { list ->
			val performances = repository.list(
				date = list.date,
				films = list.filmEDIs,
				cinemas = list.cinemaIDs
			)
			cached { call.respond(performances) }
		}
	}
}
