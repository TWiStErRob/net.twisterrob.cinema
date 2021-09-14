package net.twisterrob.cinema.cineworld.backend.endpoint.film

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.app.FeatureToggles
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.GraphFilmRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.QuickbookServiceGraph
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.cinema.cineworld.quickbook.QuickbookService
import net.twisterrob.cinema.cineworld.quickbook.QuickbookServiceNetwork
import java.time.LocalDate
import javax.inject.Provider

object Films {

	object Routes {

		@Location("/film")
		data class ListFilms(val cinemaIDs: List<Long>, val date: LocalDate) : LocationRoute

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
	abstract class BackendModule {

		@Binds
		abstract fun repository(impl: GraphFilmRepository): FilmRepository

		companion object {

			@Provides
			fun quickbook(
				featureToggles: FeatureToggles,
				graph: Provider<QuickbookServiceGraph>,
				network: Provider<QuickbookServiceNetwork>
			): QuickbookService =
				if (featureToggles.useQuickBook) {
					network.get()
				} else {
					graph.get()
				}
		}
	}
}
