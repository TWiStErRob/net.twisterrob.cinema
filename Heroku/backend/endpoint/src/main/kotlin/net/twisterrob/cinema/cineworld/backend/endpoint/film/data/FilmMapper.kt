package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import javax.inject.Inject

import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

class FilmMapper @Inject constructor(
) {

	fun map(db: DBFilm): FrontendFilm =
		FrontendFilm(
			title = db.title,
			cineworldID = db.edi,
			_created = db._created,
			_updated = db._updated
		)
}
