package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import javax.inject.Inject

import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

class FilmMapper @Inject constructor(
) {

	fun map(db: DBFilm): FrontendFilm =
		FrontendFilm(
			title = db.title,
			cineworldID = db.cineworldID,
			_created = db._created,
			_updated = db._updated,
			director = db.director,
			release = db.release,
			format = db.format,
			runtime = db.runtime,
			poster_url = db.poster_url,
			cineworldInternalID = db.cineworldInternalID,
			cert = db.cert,
			`3D` = db.`3D`,
			imax = db.imax,
			film_url = db.film_url,
			edi = db.edi,
			classification = db.classification,
			trailer = db.trailer,
			actors = db.actors,
			originalTitle = db.originalTitle,
			categories = db.categories,
			weighted = db.weighted,
			slug = db.slug,
			group = db.group
		)
}
