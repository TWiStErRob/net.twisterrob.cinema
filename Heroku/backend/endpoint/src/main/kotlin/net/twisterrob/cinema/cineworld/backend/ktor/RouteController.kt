package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import io.ktor.server.routing.Routing

/**
 * Base class for Controllers handling routes.
 *
 * Offers a breeding ground for extensions in cases where multiple receivers are needed
 * (e.g. extension on a type + application).
 */
abstract class RouteController(
	protected val application: Application
) {

	/**
	 * What is the order in which to call the [registerRoutes] method, lower goes first.
	 * Used to define some kind of consistency between runs, rather than relying on [HashSet].
	 */
	open val order: Int get() = 0

	/**
	 * Method that subtypes must override to register the handled [Routing] routes.
	 * @see RouteControllerRegistrar
	 */
	abstract fun Routing.registerRoutes()

	/**
	 * Shortcut to get the url of a [LocationRoute] based on [Resource.path].
	 *
	 * @see io.ktor.server.resources.href similar to this, but flipped
	 *                                    (`application.href(it)` vs `it.href` inside a [RouteController])
	 */
	protected inline fun <reified T : LocationRoute> T.href(): String =
		application.href(this)
}
