package net.twisterrob.cinema.frontend.test.framework

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.openqa.selenium.WebDriver
import org.openqa.selenium.logging.LogEntries
import org.openqa.selenium.logging.LogEntry
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.Logs
import java.util.logging.Level
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WebDriver_verifyLogsKtTest {

	@Test fun `no logs pass and print nothing`() {
		val driver: WebDriver = fakeDriverWithLogs()

		val log: (LogEntry) -> Unit = mock()
		driver.verifyLogs(log)

		verifyNoInteractions(log)
	}

	@ArgumentsSource(JULLevelProvider::class)
	@ParameterizedTest
	fun `single log fails regardless of level`(level: Level) {
		val log1 = entry(level)
		val driver: WebDriver = fakeDriverWithLogs(log1)

		val log: (LogEntry) -> Unit = mock()
		assertThrows<AssertionError> { driver.verifyLogs(log) }

		verify(log).invoke(log1)
		verifyNoMoreInteractions(log)
	}

	@Test
	fun `multiple logs fail`() {
		val log1 = entry()
		val log2 = entry()
		val log3 = entry()
		val driver: WebDriver = fakeDriverWithLogs(log1, log2, log3)

		val log: (LogEntry) -> Unit = mock()
		assertThrows<AssertionError> { driver.verifyLogs(log) }

		inOrder(log) {
			verify(log).invoke(log1)
			verify(log).invoke(log2)
			verify(log).invoke(log3)
			verifyNoMoreInteractions()
		}
	}

	@Test
	fun `slow network log is ignored`() {
		val slowNetwork = LogEntry(
			Level.INFO,
			Random.nextLong(),
			"http://127.0.0.1:8080/planner/index.bundle.js " +
					"5286 Slow network is detected. " +
					"See https://www.chromestatus.com/feature/5636954674692096 for more details. " +
					"Fallback font will be used while loading: " +
					"http://127.0.0.1:8080/fonts/glyphicons-halflings-regular-be810be3a3e14c682a25.woff2"
		)
		val log1 = entry()
		val log2 = entry()
		val driver: WebDriver = fakeDriverWithLogs(log1, slowNetwork, log2)

		val log: (LogEntry) -> Unit = mock()
		assertThrows<AssertionError> { driver.verifyLogs(log) }

		inOrder(log) {
			verify(log).invoke(log1)
			verify(log).invoke(log2)
			verifyNoMoreInteractions()
		}
	}
}

private fun entry(level: Level = Level.WARNING): LogEntry =
	@OptIn(ExperimentalUuidApi::class)
	LogEntry(level, Random.nextLong(), Uuid.random().toString())

private fun fakeDriverWithLogs(vararg logs: LogEntry): WebDriver {
	val logEntries = LogEntries(logs.toList())
	val mockDriver: WebDriver = mock()
	val mockOptions: WebDriver.Options = mock()
	val mockLogs: Logs = mock()
	whenever(mockDriver.manage()).thenReturn(mockOptions)
	whenever(mockDriver.manage().logs()).thenReturn(mockLogs)
	whenever(mockDriver.manage().logs().get(LogType.BROWSER)).thenReturn(logEntries)
	return mockDriver
}

private class JULLevelProvider : ArgumentsProvider {

	override fun provideArguments(parameters: ParameterDeclarations, context: ExtensionContext): Stream<out Arguments> =
		Stream.of(
			Level.OFF,
			Level.SEVERE,
			Level.WARNING,
			Level.INFO,
			Level.CONFIG,
			Level.FINE,
			Level.FINER,
			Level.FINEST,
			Level.ALL
		).map { Arguments.of(it) }
}
