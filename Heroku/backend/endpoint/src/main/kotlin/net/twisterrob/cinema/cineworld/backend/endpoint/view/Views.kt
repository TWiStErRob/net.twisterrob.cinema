package net.twisterrob.cinema.cineworld.backend.endpoint.view

import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute

object Views {

	object Routes {

		@Location("/film/{edi}/view")
		data class AddView(val edi: Long) : LocationRoute

		@Location("/film/{edi}/view")
		data class RemoveView(val edi: Long) : LocationRoute

		@Location("/film/{edi}/ignore")
		data class IgnoreView(val edi: Long) : LocationRoute
	}
}
