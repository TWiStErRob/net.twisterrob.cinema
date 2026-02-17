package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.util.AttributeKey

class HeaderLoggingFeature private constructor() {
	companion object Feature :
		BaseApplicationPlugin<ApplicationCallPipeline, HeaderLoggingConfiguration, HeaderLoggingFeature> {

		override val key = AttributeKey<HeaderLoggingFeature>("HeaderLoggingFeature")

		override fun install(
			pipeline: ApplicationCallPipeline,
			configure: HeaderLoggingConfiguration.() -> Unit
		): HeaderLoggingFeature {
			pipeline.intercept(ApplicationCallPipeline.Monitoring) {
				call.request.headers.forEach { name, values ->
					call.application.log.trace("Header $name: ${values.joinToString()}")
				}
			}
			return HeaderLoggingFeature()
		}
	}
}
class HeaderLoggingConfiguration
