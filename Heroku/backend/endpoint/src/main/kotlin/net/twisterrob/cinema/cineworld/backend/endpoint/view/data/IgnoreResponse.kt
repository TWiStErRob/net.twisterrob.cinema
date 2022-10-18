package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class IgnoreResponse(
	@get:JsonProperty("film")
	val film: Film,

	@get:JsonProperty("reason")
	val reason: String,

	@get:JsonProperty("date")
	val date: OffsetDateTime,
) {

	data class Film(
		@get:JsonProperty("edi")
		val edi: Long,
	)
}
