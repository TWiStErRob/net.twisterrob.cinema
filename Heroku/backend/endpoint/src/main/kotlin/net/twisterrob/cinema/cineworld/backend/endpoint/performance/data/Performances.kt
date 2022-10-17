package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.time.OffsetDateTime

data class Performances(
	@get:JsonProperty("date")
	val date: OffsetDateTime,

	@get:JsonProperty("cinema")
	val cinema: Long,

	@get:JsonProperty("film")
	val film: Long,

	@get:JsonProperty("performances")
	val performances: List<Performance>,
) {

	data class Performance(
		@get:JsonProperty("time")
		val time: OffsetDateTime,

		@get:JsonProperty("available")
		val available: Boolean,

		@get:JsonProperty("booking_url")
		val bookingUrl: URI,

		@get:JsonProperty("type")
		val type: String,

		@get:JsonProperty("ad")
		val isAudioDescribed: Boolean,

		@get:JsonProperty("ss")
		val isSuperScreen: Boolean,

		@get:JsonProperty("subtitled")
		val isSubtitled: Boolean,
	)
}
