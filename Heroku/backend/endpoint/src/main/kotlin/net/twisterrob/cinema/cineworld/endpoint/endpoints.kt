package net.twisterrob.cinema.cineworld.endpoint

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType.Text
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.pipeline.ContextDsl

@ContextDsl
internal fun Application.endpoints() {
	routing {
		get("/") {
			call.respondText("I am Groot!", Text.Html)
		}
		get("/resp") {
			call.respond(mapOf("hello" to "world"))
		}
		static("/static") {
			resources("static")
		}
	}
}
