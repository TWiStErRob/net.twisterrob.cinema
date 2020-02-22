package net.twisterrob.cinema.cineworld.quickbook

data class FilmsResponse<T : QuickbookFilm>(
	val films: List<T>
)
