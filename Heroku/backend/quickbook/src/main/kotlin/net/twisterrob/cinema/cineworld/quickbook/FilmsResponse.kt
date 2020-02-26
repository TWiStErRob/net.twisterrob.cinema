package net.twisterrob.cinema.cineworld.quickbook

internal data class FilmsResponse<T : QuickbookFilm>(
	val films: List<T>,
	override val errors: List<String>?
):QuickbookErrors
