package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.log
import io.ktor.server.request.path
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Routing
import kotlinx.coroutines.launch
import net.twisterrob.cinema.cineworld.backend.ktor.Env
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.backend.ktor.environment
import net.twisterrob.cinema.cineworld.backend.ktor.fakeRootFolder
import java.io.File
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

		val fakeContentPlugin = createRouteScopedPlugin("FakeContentPlugin") {
			onCall { call ->
				val fullPathAndQuery = call.request.uri.ending
				val fakeFullPathAndQueryFile = root.resolve(fullPathAndQuery)
				if (fakeFullPathAndQueryFile.exists()) {
					call.respondFake(fakeFullPathAndQueryFile)
					return@onCall
				}

				val fullPath = call.request.path().ending
				val fakeFullPathFile = root.resolve(fullPath)
				if (fakeFullPathFile.exists()) {
					call.respondFake(fakeFullPathFile)
					return@onCall
				}

				// no fake found, respond normally
			}
		}

		install(fakeContentPlugin)
	}

	private suspend fun ApplicationCall.respondFake(path: File) {
		application.log.warn("Fake response to ${request.uri} with ${path.canonicalPath}")
		response.header(HttpHeaders.XForwardedServer, "fakes")
		respondFile(path)
	}
}

private val String.ending: String
	get() = substringAfter("/").encodeURLPath().ifBlank { "index.html" }
