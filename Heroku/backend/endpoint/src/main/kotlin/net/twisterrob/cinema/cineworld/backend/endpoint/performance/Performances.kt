package net.twisterrob.cinema.cineworld.backend.endpoint.performance

import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute

object Performances {

	object Routes {

		@Location("/performance")
		object ListPerformances : LocationRoute
	}
}
