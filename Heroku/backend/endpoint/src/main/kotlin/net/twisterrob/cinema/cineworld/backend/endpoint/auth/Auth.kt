package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.client.HttpClient
import io.ktor.locations.Location
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
