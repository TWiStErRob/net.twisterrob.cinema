package net.twisterrob.cinema.cineworld.backend.endpoint.view

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.GraphViewRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewRepository
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

object Views {

	object Routes {

		@Serializable
		@Resource("/film/{edi}/view")
		data class AddView(val edi: Long/*, val cinema: Long, val date: Long*/) : LocationRoute

		@Serializable
		@Resource("/film/{edi}/view")
		data class RemoveView(val edi: Long, val cinema: Long, val date: Long) : LocationRoute {

			val time: OffsetDateTime
				get() = OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"))
		}

		@Serializable
		@Resource("/film/{edi}/ignore")
		data class IgnoreView(val edi: Long, val reason: String) : LocationRoute

		// TODO remove this in favor of Location POST properties, but frontend needs to unwrap the object in payload
		data class ViewPayload(
			val edi: Long,
			val cinema: Long,
			val date: Long
		) {

			val time: OffsetDateTime
				get() = OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"))
		}
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
