package net.twisterrob.cinema.cineworld.quickbook

internal data class CinemasResponse<T : QuickbookCinema>(
	val cinemas: List<T>,
	override val errors: List<String>?
):QuickbookErrors
