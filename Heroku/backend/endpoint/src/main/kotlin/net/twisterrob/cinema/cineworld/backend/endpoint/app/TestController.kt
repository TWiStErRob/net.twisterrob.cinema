package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath
import io.ktor.server.request.path
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Routing
import io.ktor.util.pipeline.PipelineContext
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.fakeRootFolder
import net.twisterrob.cinema.cineworld.backend.ktor.Env
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.environment
import java.io.File
import javax.inject.Inject

class TestController @Inject constructor(
	application: Application
) : RouteController(application) {

	override val order: Int get() = Int.MIN_VALUE

	override fun Routing.registerRoutes() {
		if (application.environment.config.environment == Env.PRODUCTION) return

		val root = application.attributes.fakeRootFolder
		application.log.debug(
			"""
				Running fake content from ${root}
				  -> ${root.absolutePath}
				  -> ${root.canonicalPath}
			""".trimIndent()
		)
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
		call.application.log.warn("Fake response to ${call.request.uri} with ${path.canonicalPath}")
		call.response.header(HttpHeaders.XForwardedServer, "fakes")
		call.respondFile(path)
		finish()
	}
}
