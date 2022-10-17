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
			cinemaUrl = db.cinema_url.toString(),
			created = db._created,
			updated = db._updated,
			isFavorited = false
		)

	fun map(favoritedDB: Map.Entry<DBCinema, Boolean>): FrontendCinema =
		map(favoritedDB.key).copy(isFavorited = favoritedDB.value)
}
