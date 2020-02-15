package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute

object Cinemas {

	object Routes {

		@Location("/cinema")
		object ListCinemas : LocationRoute

		@Location("/cinema/favs")
		object ListFavoriteCinemas : LocationRoute

		@Location("/cinema/{cinema}/favorite")
		data class AddFavorite(val edi: Long) : LocationRoute

		@Location("/cinema/{cinema}/favorite")
		data class RemoveFavorite(val edi: Long) : LocationRoute
	}
}
