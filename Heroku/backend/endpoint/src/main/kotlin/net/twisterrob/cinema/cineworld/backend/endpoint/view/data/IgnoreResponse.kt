package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import java.time.OffsetDateTime

data class IgnoreResponse(
	val film: Film,
	val reason: String,
	val date: OffsetDateTime
) {

	data class Film(val edi: Long)
}
