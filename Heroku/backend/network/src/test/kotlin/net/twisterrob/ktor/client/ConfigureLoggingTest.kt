package net.twisterrob.ktor.client

import com.nhaarman.mockitokotlin2.atMost
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import net.twisterrob.test.TagFunctional
import net.twisterrob.test.captureSingle
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.Logger

@TagFunctional
class ConfigureLoggingTest {

	private val mockLogger: Logger = mock()

	/**
	 * Needs to be initialized after mock stubs, because it calls [mockLogger] methods.
	 */
	private val sut: HttpClient by lazy {
		HttpClient(mockEngine()).config {
			// Real sut is the extension method.
			configureLogging(mockLogger)
		}
	}

	private fun HttpClient.stubFakeRequestResponse() {
		stub { request ->
			when {
				request.url.toString() == "http://localhost/stubbed" -> {
					respond(
						content = "Fake content",
						headers = headersOf(
							"X-Custom-Response-Header" to listOf("x-Custom-Request-Value"),
							HttpHeaders.ContentType to listOf(ContentType.Application.Xml.toString()),
						)
					)
				}

				else -> error("Unhandled ${request.url}")
			}
		}
	}

	@Test fun `info logging displays basic information`() {
		whenever(mockLogger.isInfoEnabled).thenReturn(true)
		sut.stubFakeRequestResponse()
		val expectedStack = Throwable().stackTrace[0].nextLine(2)

		val result = runBlocking {
			sut.get<String>("http://localhost/stubbed") {
				header("X-Custom-Request-Header", "x-custom-request-value")
			}
		}

		assertEquals("Fake content", result)

		inOrder(mockLogger) {
			val ex: Throwable = captureSingle {
				verify(mockLogger).debug(eq("Network call: http://localhost/stubbed"), capture())
			}
			assertEquals("net.twisterrob.ktor.client.NetworkCall", ex.javaClass.name)
			assertEquals("Callsite for http://localhost/stubbed", ex.message)
			assertEquals(expectedStack.toString(), ex.stackTrace[1].toString())
			verify(mockLogger).info("REQUEST: http://localhost/stubbed")
			verify(mockLogger).info("METHOD: HttpMethod(value=GET)")
			verify(mockLogger).info("RESPONSE: 200 OK")
			verify(mockLogger).info("METHOD: HttpMethod(value=GET)")
			verify(mockLogger).info("FROM: http://localhost/stubbed")
		}
		verifyNoMoreLogLevelInteractions(mockLogger, Logger::info)
	}

	@Test fun `debug logging displays detailed information`() {
		whenever(mockLogger.isDebugEnabled).thenReturn(true)
		sut.stubFakeRequestResponse()
		val expectedStack = Throwable().stackTrace[0].nextLine(2)

		val result = runBlocking {
			sut.get<String>("http://localhost/stubbed") {
				header("X-Custom-Request-Header", "x-custom-request-value")
			}
		}

		assertEquals("Fake content", result)

		inOrder(mockLogger) {
			val ex: Throwable = captureSingle {
				verify(mockLogger).debug(eq("Network call: http://localhost/stubbed"), capture())
			}
			assertEquals("net.twisterrob.ktor.client.NetworkCall", ex.javaClass.name)
			assertEquals("Callsite for http://localhost/stubbed", ex.message)
			assertEquals(expectedStack.toString(), ex.stackTrace[1].toString())
			verify(mockLogger).debug("REQUEST: http://localhost/stubbed")
			verify(mockLogger).debug("METHOD: HttpMethod(value=GET)")
			verify(mockLogger).debug("BODY Content-Type: null")
			verify(mockLogger).debug("BODY START")
			verify(mockLogger).debug("")
			verify(mockLogger).debug("BODY END")
			verify(mockLogger).debug("RESPONSE: 200 OK")
			verify(mockLogger).debug("METHOD: HttpMethod(value=GET)")
			verify(mockLogger).debug("FROM: http://localhost/stubbed")
			verify(mockLogger).debug("BODY Content-Type: application/xml")
			verify(mockLogger).debug("BODY START")
			verify(mockLogger).debug("Fake content")
			verify(mockLogger).debug("BODY END")
		}
		verifyNoMoreLogLevelInteractions(mockLogger, Logger::debug)
	}
	@Test fun `trace logging displays all information`() {
		whenever(mockLogger.isTraceEnabled).thenReturn(true)
		sut.stubFakeRequestResponse()
		val expectedStack = Throwable().stackTrace[0].nextLine(2)

		val result = runBlocking {
			sut.get<String>("http://localhost/stubbed") {
				header("X-Custom-Request-Header", "x-custom-request-value")
			}
		}

		assertEquals("Fake content", result)

		inOrder(mockLogger) {
			val ex: Throwable = captureSingle {
				verify(mockLogger).debug(eq("Network call: http://localhost/stubbed"), capture())
			}
			assertEquals("net.twisterrob.ktor.client.NetworkCall", ex.javaClass.name)
			assertEquals("Callsite for http://localhost/stubbed", ex.message)
			assertEquals(expectedStack.toString(), ex.stackTrace[1].toString())
			verify(mockLogger).trace("REQUEST: http://localhost/stubbed")
			verify(mockLogger).trace("METHOD: HttpMethod(value=GET)")
			verify(mockLogger).trace("COMMON HEADERS")
			verify(mockLogger).trace("-> Accept: */*")
			verify(mockLogger).trace("-> Accept-Charset: UTF-8")
			verify(mockLogger).trace("-> X-Custom-Request-Header: x-custom-request-value")
			verify(mockLogger).trace("CONTENT HEADERS")
			verify(mockLogger).trace("-> Content-Length: 0")
			verify(mockLogger).trace("BODY Content-Type: null")
			verify(mockLogger).trace("BODY START")
			verify(mockLogger).trace("")
			verify(mockLogger).trace("BODY END")
			verify(mockLogger).trace("RESPONSE: 200 OK")
			verify(mockLogger).trace("METHOD: HttpMethod(value=GET)")
			verify(mockLogger).trace("FROM: http://localhost/stubbed")
			verify(mockLogger).trace("COMMON HEADERS")
			verify(mockLogger).trace("-> Content-Type: application/xml")
			verify(mockLogger).trace("-> X-Custom-Response-Header: x-Custom-Request-Value")
			verify(mockLogger).trace("BODY Content-Type: application/xml")
			verify(mockLogger).trace("BODY START")
			verify(mockLogger).trace("Fake content")
			verify(mockLogger).trace("BODY END")
		}
		verifyNoMoreLogLevelInteractions(mockLogger, Logger::trace)
	}

	private fun verifyNoMoreLogLevelInteractions(mockLogger: Logger, method: Logger.(String) -> Unit) {
		verify(mockLogger, atMost(1)).isErrorEnabled
		verify(mockLogger, atMost(1)).isWarnEnabled
		verify(mockLogger, atMost(1)).isInfoEnabled
		verify(mockLogger, atMost(1)).isDebugEnabled
		verify(mockLogger, atMost(1)).isTraceEnabled
		try {
			verifyNoMoreInteractions(mockLogger)
		} catch (ex: AssertionError) {
			try {
				verify(mockLogger).method("Show all invocations")
			} catch (ex2: AssertionError) {
				throw ex.initCause(ex2)
			}
		}
	}
}

private fun StackTraceElement.nextLine(lines: Int): StackTraceElement =
	StackTraceElement(
		className,
		methodName,
		fileName,
		lineNumber + lines,
	)
