package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath
import io.ktor.request.path
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respondFile
import io.ktor.routing.Routing
import io.ktor.util.pipeline.PipelineContext
import net.twisterrob.cinema.cineworld.backend.ktor.Env
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.environment
import java.io.File
import javax.inject.Inject

class TestController @Inject constructor(
	application: Application
) : RouteController(application) {

	override fun Routing.registerRoutes() {
		if (application.environment.config.environment == Env.PRODUCTION) return

		val root = File("backend/src/test/fake")
		intercept(ApplicationCallPipeline.Call) {
			val fullPathAndQuery = this.call.request.uri.substringAfter("/").encodeURLPath()
				.ifBlank { "index.html" }
			val fakeFullPathAndQueryFile = root.resolve(fullPathAndQuery)
			if (fakeFullPathAndQueryFile.exists()) {
				respondFake(fakeFullPathAndQueryFile)
				return@intercept
			}

			val fullPath = this.call.request.path().substringAfter("/").encodeURLPath()
				.ifBlank { "index.html" }
			val fakeFullPathFile = root.resolve(fullPath)
			if (fakeFullPathFile.exists()) {
				respondFake(fakeFullPathFile)
				return@intercept
			}

			// no fake found, respond normally
		}
	}

	private suspend fun PipelineContext<Unit, ApplicationCall>.respondFake(path: File) {
		call.application.log.warn("Fake response to ${call.request.uri} with ${path.absolutePath}")
		call.response.header(HttpHeaders.XForwardedServer, "fakes")
		call.respondFile(path)
		finish()
	}
}
