package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.Routing
import net.twisterrob.cinema.cineworld.backend.ktor.Env
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.environment
import net.twisterrob.cinema.cineworld.backend.ktor.fakeRootFolder
import javax.inject.Inject

class TestController @Inject constructor(
	application: Application
) : RouteController(application) {

	override val order: Int get() = Int.MIN_VALUE

	@Suppress("LabeledExpression") // https://github.com/detekt/detekt/issues/5132
	override fun Routing.registerRoutes() {
		if (application.environment.config.environment == Env.PRODUCTION) return

		val root = application.environment.config.fakeRootFolder
		application.log.debug(
			"""
				Running fake content from ${root}
				  -> ${root.absolutePath}
				  -> ${root.canonicalPath}
			""".trimIndent()
		)
		install(FakeContentPlugin) {
			this.root = root
		}
	}
}
