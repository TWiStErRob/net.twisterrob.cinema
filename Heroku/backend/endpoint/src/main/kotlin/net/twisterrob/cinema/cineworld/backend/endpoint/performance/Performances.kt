package net.twisterrob.cinema.cineworld.backend.endpoint.performance

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.server.locations.Location
import net.twisterrob.cinema.cineworld.backend.app.FeatureToggles
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.GraphPerformanceRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.PerformanceRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.QuickbookPerformanceRepository
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import java.time.LocalDate
import javax.inject.Provider

object Performances {

	object Routes {

		@Location("/performance")
		data class ListPerformances(
			val date: LocalDate,
			val cinemaIDs: List<Long>,
			val filmEDIs: List<Long>
		) : LocationRoute
	}

	/**
	 * Published dependencies in this route group.
	 */
	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: PerformanceController): RouteController
	}

	/**
	 * Internal dependencies in this route group.
	 */
	@Module
	object BackendModule {

		@Provides
		fun repository(
			featureToggles: FeatureToggles,
			graph: Provider<GraphPerformanceRepository>,
			network: Provider<QuickbookPerformanceRepository>,
		): PerformanceRepository =
			if (featureToggles.useQuickBook) {
				network.get()
			} else {
				graph.get()
			}
	}
}
