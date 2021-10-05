package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.application.ApplicationCall
import io.ktor.features.origin
import io.ktor.request.host
import io.ktor.request.port

private const val DEFAULT_PORT_UNSECURE: Int = 80
private const val DEFAULT_PORT_SECURE: Int = 443

/**
 * Returns an absolute URL related to the call.
 * @param path optional directory, file, query, hash etc. to append. There's no checks on what it is.
 */
fun ApplicationCall.absoluteUrl(path: String = "/"): String {
	val defaultPort = if (request.origin.scheme == "http") DEFAULT_PORT_UNSECURE else DEFAULT_PORT_SECURE
	val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
	val protocol = request.origin.scheme
	return "$protocol://$hostPort$path"
}
