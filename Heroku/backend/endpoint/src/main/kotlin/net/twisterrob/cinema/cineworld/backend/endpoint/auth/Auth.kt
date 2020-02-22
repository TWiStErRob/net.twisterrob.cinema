package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.application.ApplicationCall
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authentication
import io.ktor.client.HttpClient
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.currentUser
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController

object Auth {

	object Routes {

		@Location("/login")
		object Login : LocationRoute

		@Location("/logout")
		object Logout : LocationRoute

		@Location("/account")
		object Account : LocationRoute

		@Location("/auth/google")
		object Google : LocationRoute

		@Location("/auth/google/return")
		object GoogleReturn : LocationRoute
	}

	/**
	 * Published dependencies in this route group.
	 */
	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: AuthController): RouteController
	}

	/**
	 * Internal dependencies in this route group.
	 */
	@Module
	object BackendModule {

		@Provides
		fun httpClient() = HttpClient()
	}
}

// TODO what's the difference to hasUser?
val ApplicationCall.isAuthenticated: Boolean
	get() = this.authentication.principal<io.ktor.auth.OAuthAccessTokenResponse.OAuth2>() != null

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
