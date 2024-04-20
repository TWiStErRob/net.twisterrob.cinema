package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

internal data class FilmsResponse<T : QuickbookFilm> @JsonCreator constructor(
	@JsonProperty("films")
	val films: List<T>,

	@JsonProperty("errors")
	override val errors: List<String>?,
) : QuickbookErrors
