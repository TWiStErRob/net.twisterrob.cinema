package net.twisterrob.cinema.cineworld.sync

import java.io.File

data class MainParameters(
	val syncCinemas: Boolean,
	val syncFilms: Boolean,
	val syncPerformances: Boolean,
	val fromFolder: File? = null,
)
