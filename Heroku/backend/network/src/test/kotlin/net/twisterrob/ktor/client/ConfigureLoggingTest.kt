package net.twisterrob.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import net.twisterrob.test.TagFunctional
import net.twisterrob.test.captureSingle
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.atMost
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @see configureLogging
 */
@TagFunctional
// Shared state: slf4j Logger instance. Spying on that would steal logs between test methods.
@Execution(ExecutionMode.SAME_THREAD)
class ConfigureLoggingTest {

	private val mockLogger: Logger = spy(LoggerFactory.getLogger(ConfigureLoggingTest::class.java)).apply {
		doReturn(false).whenever(this).isErrorEnabled
		doReturn(false).whenever(this).isWarnEnabled
		doReturn(false).whenever(this).isInfoEnabled
		doReturn(false).whenever(this).isDebugEnabled
		doReturn(false).whenever(this).isTraceEnabled
	}

	/**
	 * Needs to be initialized after mock stubs, because it calls [mockLogger] methods.
	 */
	private val sut: HttpClient by lazy {
		HttpClient(mockEngine()).config {
			// Real sut is the extension method.
			configureLogging(mockLogger)
		}
	}

	companion object {

		@Suppress("unused")
		@BeforeAll @JvmStatic fun forceSlf4jInitialization() {
			// Uninitialized [LoggerFactory.getLogger] would return different types of loggers on each call.
			LoggerFactory.getILoggerFactory()
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
		doReturn(true).whenever(mockLogger).isInfoEnabled
		sut.stubFakeRequestResponse()
		val expectedStack = Throwable().stackTrace[0].nextLine(2)

		val result = runBlocking {
			sut.get("http://localhost/stubbed") {
				header("X-Custom-Request-Header", "x-custom-request-value")
			}.bodyAsText()
		}

		assertEquals("Fake content", result)
		verifyCallSite(expectedStack)
		assertEquals(
			"""
			REQUEST: http://localhost/stubbed
			METHOD: HttpMethod(value=GET)
			RESPONSE: 200 OK
			METHOD: HttpMethod(value=GET)
			FROM: http://localhost/stubbed
			""".trimIndent(),
			verifyAllLogsFor(Logger::info)
		)
		verifyNoMoreLogLevelInteractions(Logger::info)
	}

	@Test fun `debug logging displays detailed information`() {
		doReturn(true).whenever(mockLogger).isDebugEnabled
		sut.stubFakeRequestResponse()
		val expectedStack = Throwable().stackTrace[0].nextLine(2)

		val result = runBlocking {
			sut.get("http://localhost/stubbed") {
				header("X-Custom-Request-Header", "x-custom-request-value")
			}.bodyAsText()
		}

		assertEquals("Fake content", result)
		verifyCallSite(expectedStack)
		assertEquals(
			"""
			REQUEST: http://localhost/stubbed
			METHOD: HttpMethod(value=GET)
			BODY Content-Type: null
			BODY START

			BODY END
			RESPONSE: 200 OK
			METHOD: HttpMethod(value=GET)
			FROM: http://localhost/stubbed
			BODY Content-Type: application/xml
			BODY START
			Fake content
			BODY END
			""".trimIndent(),
			verifyAllLogsFor(Logger::debug)
		)
		verifyNoMoreLogLevelInteractions(Logger::debug)
	}

	@Test fun `trace logging displays all information`() {
		doReturn(true).whenever(mockLogger).isTraceEnabled
		sut.stubFakeRequestResponse()
		val expectedStack = Throwable().stackTrace[0].nextLine(2)

		val result = runBlocking {
			sut.get("http://localhost/stubbed") {
				header("X-Custom-Request-Header", "x-custom-request-value")
			}.bodyAsText()
		}

		assertEquals("Fake content", result)
		verifyCallSite(expectedStack)
		assertEquals(
			"""
			REQUEST: http://localhost/stubbed
			METHOD: HttpMethod(value=GET)
			COMMON HEADERS
			-> Accept: */*
			-> Accept-Charset: UTF-8
			-> X-Custom-Request-Header: x-custom-request-value
			CONTENT HEADERS
			-> Content-Length: 0
			BODY Content-Type: null
			BODY START
			
			BODY END
			RESPONSE: 200 OK
			METHOD: HttpMethod(value=GET)
			FROM: http://localhost/stubbed
			COMMON HEADERS
			-> Content-Type: application/xml
			-> X-Custom-Response-Header: x-Custom-Request-Value
			BODY Content-Type: application/xml
			BODY START
			Fake content
			BODY END
			""".trimIndent(),
			verifyAllLogsFor(Logger::trace)
		)
		verifyNoMoreLogLevelInteractions(Logger::trace)
	}

	private fun verifyCallSite(expectedStack: StackTraceElement) {
		val ex: Throwable = captureSingle {
			verify(mockLogger).debug(eq("Network call: http://localhost/stubbed"), capture())
		}
		assertEquals("net.twisterrob.ktor.client.NetworkCall", ex.javaClass.name)
		assertEquals("Callsite for http://localhost/stubbed", ex.message)
		assertEquals(expectedStack.toString(), ex.stackTrace[1].toString())
	}

	private fun verifyAllLogsFor(method: Logger.(String) -> Unit): String {
		val argumentCaptor = argumentCaptor<String> {
			verify(mockLogger, atLeast(0)).method(capture())
		}
		return argumentCaptor
			.allValues
			.joinToString("\n")
	}

	private fun verifyNoMoreLogLevelInteractions(method: Logger.(String) -> Unit) {
		verify(mockLogger, atMost(1)).isErrorEnabled
		verify(mockLogger, atMost(1)).isWarnEnabled
		verify(mockLogger, atMost(1)).isInfoEnabled
		verify(mockLogger, atMost(1)).isDebugEnabled
		verify(mockLogger, atMost(1)).isTraceEnabled
		@Suppress("SwallowedException")
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
