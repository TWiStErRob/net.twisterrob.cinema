package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.time.LocalTime

data class QuickbookPerformance(
	@JsonProperty("time")
	val time: LocalTime,

	@JsonProperty("available")
	val available: Boolean,

	@JsonProperty("booking_url")
	val bookingUrl: URI,

	@JsonProperty("type")
	val type: String,

	@JsonProperty("ad")
	val isAudioDescribed: Boolean,

	@JsonProperty("ss")
	val isSuperScreen: Boolean,

	@JsonProperty("subtitled")
	val isSubtitled: Boolean,
)
