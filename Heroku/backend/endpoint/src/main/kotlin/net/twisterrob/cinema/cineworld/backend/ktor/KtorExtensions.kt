package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.origin
import io.ktor.server.request.host
import io.ktor.server.request.port

private const val DEFAULT_PORT_UNSECURE: Int = 80
private const val DEFAULT_PORT_SECURE: Int = 443

/**
 * Returns an absolute URL related to the call.
 * @param path optional directory, file, query, hash etc. to append. There's no checks on what it is.
 */
fun ApplicationCall.absoluteUrl(path: String = "/"): String {
	val defaultPort = if (request.origin.scheme == "http") DEFAULT_PORT_UNSECURE else DEFAULT_PORT_SECURE
	val hostPort = request.host() + request.port().let { port ->
		// isProduction() to prevent:
		// > Error 400: redirect_uri_mismatch
		// > You can't sign in to this app because it doesn't comply with Google's OAuth 2.0 policy.
		// > redirect_uri: http://cinema.twisterrob.net:6741/auth/google/return
		if (port == defaultPort || isProduction()) "" else ":$port"
	}
	val protocol = request.origin.scheme
	return "$protocol://$hostPort$path"
}

private fun ApplicationCall.isProduction(): Boolean =
	application.environment.config.environment == Env.PRODUCTION
