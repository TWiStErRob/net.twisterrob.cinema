package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.application.Application
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.Routing
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import java.io.File
import javax.inject.Inject

class AppController @Inject constructor(
	application: Application
) : RouteController(application) {

	override fun Routing.registerRoutes() {

		static("/") {
			// `./` is Heroku project root folder
			staticRootFolder = File("./deploy/static")
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
	}
}
