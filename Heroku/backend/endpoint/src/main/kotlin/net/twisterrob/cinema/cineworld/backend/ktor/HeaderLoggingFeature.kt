package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext

class HeaderLoggingFeature private constructor() {
	companion object Feature :
		BaseApplicationPlugin<ApplicationCallPipeline, HeaderLoggingConfiguration, HeaderLoggingFeature> {

		override val key = AttributeKey<HeaderLoggingFeature>("HeaderLoggingFeature")

		override fun install(
			pipeline: ApplicationCallPipeline,
			configure: HeaderLoggingConfiguration.() -> Unit
		): HeaderLoggingFeature {
			pipeline.intercept(ApplicationCallPipeline.Monitoring) { logRequestHeaders() }
			return HeaderLoggingFeature()
		}
	}
}

class HeaderLoggingConfiguration

private fun PipelineContext<Unit, ApplicationCall>.logRequestHeaders() {
	call.request.headers.forEach { name, values ->
		call.application.log.trace("Header $name: ${values.joinToString()}")
	}
}
