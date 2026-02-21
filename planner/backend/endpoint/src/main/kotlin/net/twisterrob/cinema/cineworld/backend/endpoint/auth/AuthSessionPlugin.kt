package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import io.ktor.server.application.PipelineCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.log
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.currentUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthSession
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.CurrentUser
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UnknownUserException
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User

internal val AuthSessionPlugin = createRouteScopedPlugin(
	name = "AuthSessionPlugin",
	createConfiguration = ::AuthSessionPluginConfig,
) {
	onCall { call ->
		val findUser = requireNotNull(pluginConfig.findUser) { "Missing way to retrieve user" }
		call.setupCurrentUserFromSession(findUser)
	}
}

private fun PipelineCall.setupCurrentUserFromSession(findUser: (String) -> User) {
	val session: AuthSession? = this.sessions.get()
	if (session != null) {
		try {
			val user = findUser(session.userId)
			this.attributes.currentUser = CurrentUser(id = user.id, email = user.email)
		} catch (ex: UnknownUserException) {
			this.application.log.error("Invalid session: {}", this.sessions, ex)
			this.sessions.clear<AuthSession>()
		}
	}
}

@Suppress("detekt.DataClassShouldBeImmutable") // Has to be mutable as far as I can see.
internal data class AuthSessionPluginConfig(
	var findUser: ((userId: String) -> User)? = null,
)
