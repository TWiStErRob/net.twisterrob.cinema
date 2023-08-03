package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewMapper
import javax.inject.Inject
import javax.inject.Provider

import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

class FilmMapper @Inject constructor(
	private val viewMapper: Provider<ViewMapper>
) {

	fun map(db: DBFilm): FrontendFilm =
		map(db, mapViews = true)

	fun map(db: DBFilm, mapViews: Boolean): FrontendFilm =
		FrontendFilm(
			title = db.title,
			cineworldID = db.cineworldID,
			created = db._created,
			updated = db._updated,
			director = db.director,
			release = db.release,
			format = db.format,
			runtime = db.runtime,
			posterUrl = db.poster_url,
			cineworldInternalID = db.cineworldInternalID,
			cert = db.cert,
			is3D = db.is3D,
			isIMAX = db.isIMAX,
			filmUrl = db.film_url,
			edi = db.edi,
			classification = db.classification,
			trailer = db.trailer,
			actors = db.actors,
			originalTitle = db.originalTitle,
			categories = db.categories,
			weighted = db.weighted,
			slug = db.slug,
			group = db.group,
			view = if (!mapViews) null else db.views.firstOrNull()?.let { viewMapper.get().map(it) }
		)
}
