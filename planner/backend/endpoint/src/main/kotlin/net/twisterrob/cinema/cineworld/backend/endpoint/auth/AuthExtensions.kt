package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.getPrincipal
import io.ktor.server.response.respondText
import io.ktor.util.Attributes
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.currentUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.CurrentUser

// TODO what's the difference to hasUser?
val ApplicationCall.isAuthenticated: Boolean
	get() = this.getPrincipal<io.ktor.server.auth.Principal>() != null

/**
 * @see userId if this is `true`, the user ID can be retrieved
 */
// TODO hasUser is true even with expired session?
val ApplicationCall.hasUser: Boolean
	get() = this.attributes.currentUser != null

suspend fun ApplicationCall.respondUserNotFound() {
	this.respondText("Can't find user.", status = HttpStatusCode.NotFound)
}

/**
 * @return ID of the currently logged-in user if [hasUser] is `true`
 * @throws NullPointerException if [hasUser] is `false`
 */
val ApplicationCall.userId: String
	get() = this.attributes.requireUser.id

val Attributes.requireUser: CurrentUser
	get() = currentUser ?: error("User is not authenticated.")

/**
 * TODO interceptor, also see configuration oath().skipWhen {}
 * ```
 * function ensureAuthenticated(req, res, next) {
 * 	if (req.isAuthenticated()) { return next(); }
 * 	res.send(401, 'Please log in first to access this feature.');
 * }
 * ```
 */
