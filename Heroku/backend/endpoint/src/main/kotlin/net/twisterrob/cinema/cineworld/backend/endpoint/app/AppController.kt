package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.http.content.default
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.staticRootFolder
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
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

		static("/") {
			staticRootFolder = application.attributes.staticRootFolder
			application.log.debug(
				"""
					Running static content at / from ${staticRootFolder}
					  -> ${staticRootFolder?.absolutePath}
					  -> ${staticRootFolder?.canonicalPath}
				""".trimIndent()
			)
			// Serve all files from staticRootFolder
			files(".")
			// `/` = `/index.html`
			default("index.html")

			// workaround for https://github.com/ktorio/ktor/issues/514#issuecomment-414982574
			// > `default()` function is not applied to subdirectories
			static("planner") {
				@Suppress("UnsafeCallOnNullableType") // staticRootFolder is set above, so assumption holds for now.
				staticRootFolder = parent!!.staticRootFolder!!.resolve("planner")
				files(".")
				default("index.html")
			}
		}
	}
}
