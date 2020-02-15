package net.twisterrob.cinema.cineworld.backend.ktor

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.locations.Locations

internal fun Application.configuration() {
	install(DefaultHeaders)
	install(CallLogging)
	install(HeaderLoggingFeature)
	install(ContentNegotiation) {
		jackson {
			enable(SerializationFeature.INDENT_OUTPUT)
		}
	}
	install(Locations) // support @Location
}
