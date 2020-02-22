package net.twisterrob.cinema.cineworld.backend.endpoint.view

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.GraphViewRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewRepository
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

object Views {

	object Routes {

		@Location("/film/{edi}/view")
		data class AddView(val edi: Long, val cinema: Long, val date: Long) : LocationRoute {

			val time: OffsetDateTime
				get() = OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"))
		}

		@Location("/film/{edi}/view")
		data class RemoveView(val edi: Long, val cinema: Long, val date: Long) : LocationRoute {

			val time: OffsetDateTime
				get() = OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"))
		}

		@Location("/film/{edi}/ignore")
		data class IgnoreView(val edi: Long) : LocationRoute
	}

	/**
	 * Published dependencies in this route group.
	 */
	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: ViewsController): RouteController
	}

	/**
	 * Internal dependencies in this route group.
	 */
	@Module
	interface BackendModule {

		@Binds
		fun repository(impl: GraphViewRepository): ViewRepository
	}
}
