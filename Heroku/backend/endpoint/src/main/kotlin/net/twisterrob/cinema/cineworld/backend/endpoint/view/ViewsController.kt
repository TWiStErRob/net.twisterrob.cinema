package net.twisterrob.cinema.cineworld.backend.endpoint.view

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.locations.delete
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.hasUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.userId
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.Cinemas
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The [Cinemas] controller. This controller handles the routes related to cinemas.
 * It inherits [RouteController] that offers some basic functionality.
 */
@Singleton
class ViewsController @Inject constructor(
	application: Application,
	private val repository: ViewRepository
) : RouteController(application) {

	/**
	 * Registers the routes related to [Cinemas].
	 */
	override fun Routing.registerRoutes() {

		post<Views.Routes.AddView> { add ->
			if (call.hasUser) {
				val view = repository.addView(call.userId, add.cinema, add.edi, add.time)
					?: return@post call.respondText("Can't find view ${add}.", status = NotFound)
				call.respond(view)
			} else {
				call.respondText("Can't find user.", status = NotFound)
			}
		}

		delete<Views.Routes.RemoveView> { remove ->
			if (call.hasUser) {
				repository.removeView(call.userId, remove.cinema, remove.edi, remove.time)
				call.respondText("", status = OK)
			} else {
				call.respondText("Can't find user.", status = NotFound)
			}
		}
	}
}
