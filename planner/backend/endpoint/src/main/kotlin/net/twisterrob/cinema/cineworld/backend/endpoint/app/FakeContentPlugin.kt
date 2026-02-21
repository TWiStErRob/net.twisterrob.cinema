package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath
import io.ktor.server.application.PipelineCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.log
import io.ktor.server.request.path
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import java.io.File

internal val FakeContentPlugin = createRouteScopedPlugin(
	name = "FakeContentPlugin",
	createConfiguration = ::FakeContentPluginConfig,
) {
	onCall { call ->
		val root = requireNotNull(this.pluginConfig.root) { "Missing root directory" }
		call.tryRespondFakeFrom(root)
	}
}

@Suppress("detekt.SuspendFunWithCoroutineScopeReceiver") // REPORT ktor, how?
private suspend fun PipelineCall.tryRespondFakeFrom(root: File) {
	val fullPathAndQuery = request.uri.ending
	val fakeFullPathAndQueryFile = root.resolve(fullPathAndQuery)
	if (fakeFullPathAndQueryFile.exists()) {
		respondFake(fakeFullPathAndQueryFile)
		return
	}

	val fullPath = request.path().ending
	val fakeFullPathFile = root.resolve(fullPath)
	if (fakeFullPathFile.exists()) {
		this.respondFake(fakeFullPathFile)
		return
	}

	// no fake found, respond normally
}

@Suppress("detekt.SuspendFunWithCoroutineScopeReceiver") // REPORT ktor, how?
private suspend fun PipelineCall.respondFake(path: File) {
	application.log.warn("Fake response to ${request.uri} with ${path.canonicalPath}")
	response.header(HttpHeaders.XForwardedServer, "fakes")
	respondFile(path)
}

@Suppress("detekt.DataClassShouldBeImmutable") // Has to be mutable as far as I can see.
internal data class FakeContentPluginConfig(
	var root: File? = null,
)

private val String.ending: String
	get() = substringAfter("/").encodeURLPath().ifBlank { "index.html" }
