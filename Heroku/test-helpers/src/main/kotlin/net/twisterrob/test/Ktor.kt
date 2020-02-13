package net.twisterrob.test

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.hostWithPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val Url.hostWithPortIfRequired: String
	get() = if (port == protocol.defaultPort) host else hostWithPort

private val Url.fullUrl: String
	get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

/**
 * Utility method to create an empty [MockEngine] that has no stubbing set up yet.
 * This is useful to follow standard testing practices where dependencies need to be injected in constructor ([BeforeEach]),
 * but stubs are only declared in each [Test].
 *
 * @param block optional handler setup
 * @see HttpClient.stub to set up individual calls
 * @sample `HttpClient(mockEngine() { reuseHandlers = false })`
 */
fun mockEngine(block: MockEngineConfig.() -> Unit = {}): MockEngine {
	val config = MockEngineConfig().apply {
		addHandler { error("Dummy Stub to satisfy MockEngine's constructor") }
	}
	return MockEngine(config).apply {
		// clear Dummy Stub after MockEngine has been created.
		config.requestHandlers.clear()
		// allow setting up configuration (e.g. reuseHandlers)
		config.apply(block)
	}
}

fun HttpClient.stub(handler: MockRequestHandler) {
	val config = this.engineConfig as MockEngineConfig
	config.addHandler(handler)
}
