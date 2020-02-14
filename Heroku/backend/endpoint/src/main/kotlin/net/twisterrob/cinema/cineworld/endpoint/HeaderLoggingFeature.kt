package net.twisterrob.cinema.cineworld.endpoint

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext

class HeaderLoggingFeature {
	companion object Feature :
		ApplicationFeature<ApplicationCallPipeline, HeaderLoggingConfiguration, HeaderLoggingFeature> {

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
		println("$name: ${values.joinToString()}")
	}
}
