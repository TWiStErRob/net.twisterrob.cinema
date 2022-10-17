package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty

data class QuickbookFilmSimple(
	@JsonProperty("edi")
	override val edi: Long,

	@JsonProperty("title")
	override val title: String,
) : QuickbookFilm
