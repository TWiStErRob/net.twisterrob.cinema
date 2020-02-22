package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.currentUser

// TODO what's the difference to hasUser?
val ApplicationCall.isAuthenticated: Boolean
	get() = this.authentication.principal != null

/**
 * @see userId if this is `true`, the user ID can be retrieved
 */
// TODO hasUser is true even with expired session?
val ApplicationCall.hasUser: Boolean
	get() = this.attributes.currentUser != null

/**
 * @return ID of the currently logged in user if [hasUser] is `true`
 * @throws NullPointerException if [hasUser] is `false`
 */
val ApplicationCall.userId: String
	get() = this.attributes.currentUser!!.id
