package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.application.Application
import io.ktor.locations.Location
import io.ktor.locations.locations
import io.ktor.routing.Routing

/**
 * Base class for Controllers handling routes.
 *
 * Offers a breeding ground for extensions in cases where multiple receivers are needed (e.g. extension on a type + application).
 */
abstract class RouteController(
	private val application: Application
) {

	/**
	 * Shortcut to get the url of a [LocationRoute] based on [Location.path]
	 * @see io.ktor.locations.href similar to this, but flipped
	 *                             (`application.href(it)` vs `it.href` inside a [RouteController])
	 */
	val LocationRoute.href
		get() = application.locations.href(this)

	/**
	 * Method that subtypes must override to register the handled [Routing] routes.
	 * @see RouteControllerRegistrar
	 */
	abstract fun Routing.registerRoutes()
}
