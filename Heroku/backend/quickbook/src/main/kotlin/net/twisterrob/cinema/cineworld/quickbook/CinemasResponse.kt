package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

internal data class CinemasResponse<T : QuickbookCinema> @JsonCreator constructor(
	@JsonProperty("cinemas")
	val cinemas: List<T>,

	@JsonProperty("errors")
	override val errors: List<String>?,
) : QuickbookErrors
