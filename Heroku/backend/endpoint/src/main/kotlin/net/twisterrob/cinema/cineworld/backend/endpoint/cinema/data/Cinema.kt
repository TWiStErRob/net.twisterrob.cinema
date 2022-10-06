package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import java.time.OffsetDateTime

data class Cinema(
	val cineworldID: Long,
	val name: String,
	val postcode: String,
	val address: String,
	val telephone: String?,
	val cinema_url: String,
	val _created: OffsetDateTime,
	val _updated: OffsetDateTime?,
	val `class`: String = "Cinema",
	val fav: Boolean = false
)
