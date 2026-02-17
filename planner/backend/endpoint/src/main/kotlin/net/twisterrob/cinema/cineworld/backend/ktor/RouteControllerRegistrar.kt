package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.routing
import javax.inject.Inject

/**
 * Magic glue that makes defining new route groups as easy as an @[dagger.multibindings.IntoSet].
 */
class RouteControllerRegistrar @Inject constructor(
	private val application: Application,
	private val controllers: Set<@JvmSuppressWildcards RouteController>
) {

	fun register() {
		application.routing {
			require(controllers.isNotEmpty()) { "There are no controllers, not starting up." }
			controllers.toList().sortedWith(CONTROLLER_ORDER).forEach { controller ->
				application.log.trace("Registering '$controller' routes...")
				controller.apply { registerRoutes() }
			}
		}
	}

	companion object {

		val CONTROLLER_ORDER: Comparator<RouteController> = compareBy(
			RouteController::order
		) { it::class.java.name }
	}
}
