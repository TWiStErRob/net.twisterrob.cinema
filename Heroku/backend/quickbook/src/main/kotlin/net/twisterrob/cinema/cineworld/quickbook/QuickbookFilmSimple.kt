package net.twisterrob.cinema.cineworld.quickbook

data class QuickbookFilmSimple(
	override val edi: Long,
	override val title: String
) : QuickbookFilm
