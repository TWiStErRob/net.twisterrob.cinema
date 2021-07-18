package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.application.Application
import io.ktor.application.log
import io.ktor.routing.routing
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
			trace { application.log.trace(it.buildText()) }
			controllers.toList().sortedBy(RouteController::order).forEach { controller ->
				application.log.trace("Registering '$controller' routes...")
				controller.apply { registerRoutes() }
			}
		}
	}
}
