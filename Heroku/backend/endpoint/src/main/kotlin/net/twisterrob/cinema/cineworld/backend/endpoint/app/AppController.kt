package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.application
import io.ktor.routing.get
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.staticRootFolder
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import javax.inject.Inject

/**
 * @see App
 */
class AppController @Inject constructor(
	application: Application
) : RouteController(application) {

	/**
	 * @see App.Routes
	 */
	override fun Routing.registerRoutes() {

		static("/") {
			// `./` is Heroku project root folder
			staticRootFolder = application.attributes.staticRootFolder
			// Serve all files from staticRootFolder
			files(".")
			// `/` = `/index.html`
			default("index.html")

			// workaround for https://github.com/ktorio/ktor/issues/514#issuecomment-414982574
			// > `default()` function is not applied to subdirectories
			static("planner") {
				staticRootFolder = parent!!.staticRootFolder!!.resolve("planner")
				files(".")
				default("index.html")
			}
		}

		get("favicon.ico") {
			call.respondRedirect("https://www.google.com/s2/favicons?domain=www.cineworld.co.uk")
		}
	}
}
