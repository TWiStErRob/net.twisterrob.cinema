package net.twisterrob.cinema.cineworld.backend.endpoint.film

import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute

object Films {

	object Routes {

		@Location("/film")
		object ListFilms : LocationRoute

		@Location("/film/{edi}")
		data class GetFilm(val edi: Long) : LocationRoute
	}
}
