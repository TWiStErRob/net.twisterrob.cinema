package net.twisterrob.cinema.cineworld.backend.endpoint.view

import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.hasUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.respondUserNotFound
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.userId
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewRepository
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Views
 */
@Singleton
class ViewsController @Inject constructor(
	application: Application,
	private val repository: ViewRepository
) : RouteController(application) {

	/**
	 * @see Views.Routes
	 */
	@Suppress("LabeledExpression") // https://github.com/detekt/detekt/issues/5132
	override fun Routing.registerRoutes() {

		post<Views.Routes.AddView> {
			if (call.hasUser) {
				val payload = call.receive<Views.Routes.ViewPayload>()
				val view = repository.addView(
					user = call.userId,
					film = payload.edi,
					cinema = payload.cinema,
					time = payload.time
				)
					?: return@post call.respondText("Can't find view ${payload}.", status = NotFound)
				call.respond(view)
			} else {
				call.respondUserNotFound()
			}
		}

		delete<Views.Routes.RemoveView> { remove ->
			if (call.hasUser) {
				repository.removeView(
					user = call.userId,
					film = remove.edi,
					cinema = remove.cinema,
					time = remove.time
				)
				call.respondText("", status = OK)
			} else {
				call.respondUserNotFound()
			}
		}

		put<Views.Routes.IgnoreView> { ignore ->
			if (call.hasUser) {
				val ignored = repository.ignoreView(
					user = call.userId,
					film = ignore.edi,
					reason = ignore.reason
				)
				call.respond(ignored)
			} else {
				call.respondUserNotFound()
			}
		}
	}
}
