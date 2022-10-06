package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.Routing
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.RouteResolveTraceFormatter
import javax.inject.Inject

class TraceController @Inject constructor(
	application: Application,
	private val traceFormatter: RouteResolveTraceFormatter,
) : RouteController(application) {

	override val order: Int get() = Int.MIN_VALUE

	override fun Routing.registerRoutes() {
		trace {
			if (application.log.isTraceEnabled) {
				application.log.trace(traceFormatter.formatFull(it))
			} else {
				application.log.info(traceFormatter.formatSimple(it))
			}
		}
	}
}
