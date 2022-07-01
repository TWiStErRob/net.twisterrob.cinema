package net.twisterrob.ktor.client

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import org.slf4j.Logger

fun HttpClientConfig<*>.configureLogging(log: Logger) {
	install(Logging) {
		when {
			log.isTraceEnabled -> {
				level = LogLevel.ALL
				logger = LevelLogger(log, Logger::trace)
			}

			log.isDebugEnabled -> {
				level = LogLevel.BODY
				logger = LevelLogger(log, Logger::debug)
			}

			log.isInfoEnabled -> {
				level = LogLevel.INFO
				logger = LevelLogger(log, Logger::info)
			}

			else -> {
				level = LogLevel.NONE
				logger = LevelLogger(log, Logger::error)
			}
		}
	}
}

private class NetworkCall(url: String) : Exception("Callsite for $url")

private class LevelLogger(
	private val log: Logger,
	private val logAtLevel: Logger.(message: String) -> Unit,
) : io.ktor.client.plugins.logging.Logger {

	override fun log(message: String) {
		if (message.startsWith("REQUEST: ")) {
			val url = message.substringAfter("REQUEST: ").substringBefore("\n")
			log.debug("Network call: $url", NetworkCall(url).apply {
				stackTrace = stackTrace
					.drop(1)
					.filterNot {
						it.className.startsWith("io.ktor.")
								|| it.className.startsWith("kotlinx.coroutines.")
								|| it.className.startsWith("io.netty.")
								|| it.className.startsWith("kotlin.coroutines.")
								|| it.className.startsWith("java.")
					}
					.toTypedArray()
			})
		}
		log.logAtLevel(message.shortenTo(MAX_LOG_LENGTH))
	}

	private fun String.shortenTo(maxLength: Int): String =
		if (this.length < maxLength) {
			this
		} else {
			this.substring(0, maxLength) + " ... [showing only ${maxLength}, ${this.length - maxLength} omitted]"
		}

	companion object {

		/**
		 * Arbitrary limit to make sure very long messages are shortened.
		 * This helps to contain the size of the logged JSONs for example, but still should allow for full stack traces.
		 */
		private const val MAX_LOG_LENGTH: Int = 5000
	}
}
