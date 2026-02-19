package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.content.TextContent
import io.ktor.http.Headers
import io.ktor.http.content.OutgoingContent
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.plugins.origin
import io.ktor.server.request.RequestAlreadyConsumedException
import io.ktor.server.request.contentCharset
import io.ktor.server.request.httpVersion
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.readText
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServerLogging(
	private val logger: Logger,
	private val level: LogLevel
) {

	@Suppress("unused")
	enum class LogLevel(
		internal val showInfo: Boolean,
		internal val showHeaders: Boolean,
		internal val showBody: Boolean
	) {

		ALL(true, true, true),
		HEADERS(true, true, false),
		BODY(true, false, true),
		INFO(true, false, false),
		NONE(false, false, false)
	}

	class Configuration {

		var logger: Logger = LoggerFactory.getLogger(ServerLogging::class.java)

		var level: LogLevel = LogLevel.INFO
	}

	private suspend fun logRequest(call: ApplicationCall) {
		logger.info(buildString {
			if (level.showInfo) {
				val requestURI = call.request.path()
				appendLine("REQUEST")
				appendLine(call.request.origin.run {
					"${method.value} $scheme://$serverHost:$serverPort$requestURI $version"
				})
			}
			if (level.showHeaders) {
				appendLine("REQUEST HEADERS")
				appendHeaders(call.request.headers)
			}
			if (level.showBody) {
				appendLine()
				appendLine("REQUEST BODY START")
				appendLine(call.requestBody() ?: "<request body omitted>")
				appendLine("REQUEST BODY END")
			}
		})
	}

	private fun logResponse(call: ApplicationCall, subject: Any) {
		logger.info(buildString {
			if (level.showInfo) {
				appendLine("RESPONSE")
				appendLine("${call.request.httpVersion} ${call.response.status() ?: "<no status>"}")
			}
			if (level.showHeaders) {
				appendLine("RESPONSE HEADERS")
				appendHeaders(call.response.headers.allValues())
			}
			if (level.showBody) {
				appendLine()
				appendLine("RESPONSE BODY START")
				appendLine((subject as OutgoingContent).asString() ?: "<response body omitted>")
				appendLine("RESPONSE BODY END")
			}
		})
	}

	private fun StringBuilder.appendHeaders(headers: Headers) {
		if (headers.isEmpty()) {
			appendLine("<none>")
		} else {
			headers.forEach { header, values ->
				appendHeader(header, values)
			}
		}
	}

	private fun StringBuilder.appendHeader(key: String, values: List<String>) {
		if (values.isEmpty()) {
			appendLine("$key: <no values>")
		} else {
			values.forEach { value ->
				appendLine("$key: $value")
			}
		}
	}

	@Suppress("unused")
	private suspend fun ApplicationCall.requestBodyAlt(): String? =
		try {
			String(receive<ByteArray>())
		} catch (e: RequestAlreadyConsumedException) {
			logger.error("Logging payloads requires install(DoubleReceive) { cacheRawRequest = true }.", e)
			null
		}

	private suspend fun ApplicationCall.requestBody(): String? {
		val charset = request.contentCharset() ?: Charsets.UTF_8
		val channel = request.receiveChannel()
		return runBlocking { channel.tryReadText(charset) }
	}

	private fun OutgoingContent.asString(): String? =
		@Suppress("OptionalWhenBraces")
		when (val content = this) {

			is OutgoingContent.NoContent -> {
				""
			}

			is TextContent -> {
				content.text
			}

			is OutgoingContent.WriteChannelContent -> {
				runBlocking {
					@Suppress("detekt.UnnecessaryFullyQualifiedName")
					// TODO https://youtrack.jetbrains.com/issue/KTOR-6030
					// interface is deprecated, so can't import, but this is a function call.
					val channel = io.ktor.utils.io.ByteChannel(true)
					content.writeTo(channel)
					channel.tryReadText(Charsets.UTF_8)
				}
			}

			is OutgoingContent.ByteArrayContent,
			is OutgoingContent.ProtocolUpgrade,
			is OutgoingContent.ReadChannelContent -> {
				logger.error("Unknown response body content: ${content::class}")
				null
			}
		}

	private suspend inline fun ByteReadChannel.tryReadText(charset: Charset): String? =
		try {
			readRemaining().use { it.readText(charset = charset) }
		} catch (@Suppress("TooGenericExceptionCaught") cause: Throwable) {
			logger.error("Cannot read text", cause)
			null
		}

	fun install(pipeline: Application) {
		pipeline.intercept(ApplicationCallPipeline.Monitoring) {
			logRequest(call)
			proceedWith(subject)
		}
		pipeline.sendPipeline.addPhase(responseLoggingPhase)
		pipeline.sendPipeline.intercept(responseLoggingPhase) {
			logResponse(call, subject)
		}
	}

	companion object Feature : BaseApplicationPlugin<Application, Configuration, ServerLogging> {

		override val key = AttributeKey<ServerLogging>("Server Logging Feature")

		private val responseLoggingPhase = PipelinePhase("ServerResponseLogging")

		override fun install(pipeline: Application, configure: Configuration.() -> Unit): ServerLogging {
			val configuration = Configuration().apply(configure)
			val instance = ServerLogging(
				logger = configuration.logger,
				level = configuration.level
			)
			instance.install(pipeline)
			return instance
		}
	}
}
