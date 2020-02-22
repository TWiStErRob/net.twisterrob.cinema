package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import java.time.OffsetDateTime

data class Film(
	val cineworldID: Long,
	val title: String,
	val _created: OffsetDateTime,
	val _updated: OffsetDateTime?,
	val `class`: String = "Film"
)
