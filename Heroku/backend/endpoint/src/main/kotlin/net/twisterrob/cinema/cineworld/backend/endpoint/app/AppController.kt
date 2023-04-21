package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.staticRootFolder
import javax.inject.Inject

/**
 * @see App
 */
class AppController @Inject constructor(
	application: Application
) : RouteController(application) {

	override val order: Int get() = -1

	/**
	 * @see App.Routes
	 */
	override fun Routing.registerRoutes() {

		get("favicon.ico") {
			call.respondRedirect("https://www.google.com/s2/favicons?domain=www.cineworld.co.uk")
		}

		val staticRootFolder = application.environment.config.staticRootFolder
		staticFiles("/", staticRootFolder) {
			application.log.debug(
				"""
					Running static content at / from ${staticRootFolder}
					  -> ${staticRootFolder.absolutePath}
					  -> ${staticRootFolder.canonicalPath}
				""".trimIndent()
			)
		}
	}
}
