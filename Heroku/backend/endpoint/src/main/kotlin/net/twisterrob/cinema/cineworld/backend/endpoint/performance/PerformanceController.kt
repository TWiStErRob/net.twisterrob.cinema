package net.twisterrob.cinema.cineworld.backend.endpoint.performance

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
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
