package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import java.net.URI
import java.time.OffsetDateTime

data class Film(
	val cineworldID: Long?,
	val title: String,
	val director: String,
	val release: OffsetDateTime,
	val format: String,
	val runtime: Long,
	val poster_url: URI,
	val cineworldInternalID: Long,
	val cert: String,
	val imax: Boolean,
	val `3D`: Boolean,
	val film_url: URI,
	val edi: Long,
	val classification: String,
	val trailer: URI?,
	val actors: String,
	val originalTitle: String,
	val categories: List<String>,
	val weighted: Long,
	val slug: String,
	val group: Long,
	val _created: OffsetDateTime,
	val _updated: OffsetDateTime?,
	val `class`: String = "Film",
	val view: View? = null
)
