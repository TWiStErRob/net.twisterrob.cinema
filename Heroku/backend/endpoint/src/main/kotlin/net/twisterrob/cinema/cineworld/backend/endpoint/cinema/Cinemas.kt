package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.GraphCinemaRepository
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController

object Cinemas {

	object Routes {

		@Serializable
		@Resource("/cinema")
		object ListCinemas : LocationRoute

		@Serializable
		@Resource("/cinema/favs")
		object ListFavoriteCinemas : LocationRoute

		@Serializable
		@Resource("/cinema/{cinema}/favorite")
		data class AddFavorite(val cinema: Long) : LocationRoute

		@Serializable
		@Resource("/cinema/{cinema}/favorite")
		data class RemoveFavorite(val cinema: Long) : LocationRoute
	}

	/**
	 * Published dependencies in this route group.
	 */
	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: CinemasController): RouteController
	}

	/**
	 * Internal dependencies in this route group.
	 */
	@Module
	interface BackendModule {

		@Binds
		fun repository(impl: GraphCinemaRepository): CinemaRepository
	}
}
