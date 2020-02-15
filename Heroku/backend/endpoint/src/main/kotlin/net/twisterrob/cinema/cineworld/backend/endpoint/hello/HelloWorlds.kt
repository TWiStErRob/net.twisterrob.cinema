package net.twisterrob.cinema.cineworld.backend.endpoint.hello

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType.Text
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import javax.inject.Inject

object HelloWorlds {

	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: Controller): RouteController
	}

	class Controller @Inject constructor(application: Application) : RouteController(application) {

		override fun Routing.registerRoutes() {
			get("/") {
				call.respondText("I am Groot!", Text.Html)
			}

			get("/resp") {
				call.respond(mapOf("hello" to "world"))
			}

			static("/static") {
				resources("static")
			}
		}
	}
}
