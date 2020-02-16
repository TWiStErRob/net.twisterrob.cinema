package net.twisterrob.cinema.cineworld.backend.ktor

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.pre
import kotlinx.html.title
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.staticRootFolder
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

internal fun Application.configuration(staticRootFolder: File = File("./deploy/static")) {
	this.attributes.staticRootFolder = staticRootFolder

	install(DefaultHeaders)
	install(CallLogging)
	install(HeaderLoggingFeature)
	install(ContentNegotiation) {
		jackson {
			enable(SerializationFeature.INDENT_OUTPUT)
		}
	}
	install(StatusPages) {
		exception<Throwable> { cause ->
			call.respondHtml(HttpStatusCode.InternalServerError) {
				head {
					title { +"Internal Server Error" }
				}
				body {
					h1 {
						+"Internal Server Error"
					}
					h2 { +"Exception" }
					pre {
						+StringWriter().apply { cause.printStackTrace(PrintWriter(this, true)) }.toString()
					}
				}
			}
			throw cause
		}
	}
	install(Locations) // support @Location
}
