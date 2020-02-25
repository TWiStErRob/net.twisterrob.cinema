package net.twisterrob.cinema.cineworld.backend.endpoint.performance

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.GraphPerformanceRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.PerformanceRepository
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import java.time.LocalDate

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
	interface BackendModule {

		@Binds
		fun repository(impl: GraphPerformanceRepository): PerformanceRepository
	}
}
