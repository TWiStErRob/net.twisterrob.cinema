package net.twisterrob.test

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.hostWithPort
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

private val Url.hostWithPortIfRequired: String
	get() = if (port == protocol.defaultPort) host else hostWithPort

@Suppress("unused") // Might be useful in the future, let's keep it.
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
	@Suppress("detekt.MissingUseCall") // It's the responsibility of the user.
	return MockEngine(config).apply {
		// clear Dummy Stub after MockEngine has been created.
		config.requestHandlers.clear()
		// allow setting up configuration (e.g. reuseHandlers)
		@Suppress("detekt.NestedScopeFunctions") // REPORT False positive: not a block, just a function call.
		config.apply(block)
	}
}

fun HttpClient.stub(handler: MockRequestHandler) {
	val config = this.engineConfig as MockEngineConfig
	config.addHandler(handler)
}

fun HttpClient.stub(url: String, handler: MockRequestHandler) {
	stub { request ->
		@Suppress("UseIfInsteadOfWhen")
		when (request.url.toString()) {
			url -> handler(this, request)
			else -> fail("Expecting $url, but request url didn't match: ${request.url}")
		}
	}
}

fun HttpClient.verify(url: String, block: (appRequest: HttpRequestData, stubbedResponse: HttpResponseData) -> Unit) {
	val engine = this.engine as MockEngine
	@Suppress("DontDowncastCollectionTypes") // No other way to support [verifyNoMoreInteractions].
	val requestHistory = engine.requestHistory as MutableList<HttpRequestData>
	val request: HttpRequestData = requestHistory.firstOrNull()
		?: fail("No more requests when trying to verify $url")
	@Suppress("DontDowncastCollectionTypes") // No other way to support [verifyNoMoreInteractions].
	val responseHistory = engine.responseHistory as MutableList<HttpResponseData>
	val response: HttpResponseData = responseHistory.firstOrNull()
		?: fail("No more responses when trying to verify $url")

	assertEquals(url, request.url.toString())
	block(request, response)

	// clean up for next verification
	requestHistory.removeAt(0)
	responseHistory.removeAt(0)
}

fun HttpClient.verifyNoInteractions() {
	verifyNoMoreInteractions()
}

fun HttpClient.verifyNoMoreInteractions() {
	val engine = this.engine as MockEngine
	val message = "No interactions were expected, but there were requests: " +
			engine.requestHistory.joinToString(prefix = "\n\t", separator = "\n\t")
	assertThat(message, engine.requestHistory, empty())
}
