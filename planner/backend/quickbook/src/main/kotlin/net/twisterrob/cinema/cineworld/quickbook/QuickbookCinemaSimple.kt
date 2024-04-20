package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty

class QuickbookCinemaSimple(
	@JsonProperty("id")
	override val id: Int,

	@JsonProperty("name")
	override val name: String,
) : QuickbookCinema
