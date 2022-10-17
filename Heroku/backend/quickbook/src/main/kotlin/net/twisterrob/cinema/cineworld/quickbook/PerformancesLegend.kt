package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty

data class PerformancesLegend(
	@JsonProperty("code")
	val code: String,

	@JsonProperty("description")
	val description: String,
)
