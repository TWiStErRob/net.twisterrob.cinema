package net.twisterrob.cinema.cineworld.backend.endpoint.auth

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController

object Auth {

	object Routes {

		@Serializable
		@Resource("/login")
		object Login : LocationRoute

		@Serializable
		@Resource("/logout")
		object Logout : LocationRoute

		@Serializable
		@Resource("/account")
		object Account : LocationRoute

		@Serializable
		@Resource("/auth/google")
		object Google : LocationRoute

		@Serializable
		@Resource("/auth/google/return")
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

//		@Provides
//		fun httpClient() = HttpClient()
	}
}
