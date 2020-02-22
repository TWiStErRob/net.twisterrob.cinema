package net.twisterrob.cinema.cineworld.backend.endpoint.film

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.GraphFilmRepository
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController

object Films {

	object Routes {

		@Location("/film")
		object ListFilms : LocationRoute

		@Location("/film/{edi}")
		data class GetFilm(val edi: Long) : LocationRoute
	}

	/**
	 * Published dependencies in this route group.
	 */
	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: FilmsController): RouteController
	}

	/**
	 * Internal dependencies in this route group.
	 */
	@Module
	interface BackendModule {

		@Binds
		fun repository(impl: GraphFilmRepository): FilmRepository
	}
}
