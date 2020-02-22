package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import javax.inject.Inject
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema as FrontendCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

class CinemaMapper @Inject constructor(
) {

	fun map(db: DBCinema): FrontendCinema =
		FrontendCinema(
			name = db.name,
			cineworldID = db.cineworldID,
			postcode = db.postcode,
			address = db.address,
			telephone = db.telephone,
			cinema_url = db.cinema_url.toString(),
			_created = db._created,
			_updated = db._updated,
			fav = false
		)

	fun map(favoritedDB: Map.Entry<DBCinema, Boolean>): FrontendCinema =
		map(favoritedDB.key).copy(fav = favoritedDB.value)
}
