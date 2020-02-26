package net.twisterrob.cinema.cineworld.quickbook

data class QuickbookErrorsException(
	val errors: List<String>
) : Exception(errors.joinToString(separator = "\n"))
