package net.twisterrob.cinema.cineworld.sync

import java.io.File

data class MainParameters(
	val isSyncCinemas: Boolean,
	val isSyncFilms: Boolean,
	val isSyncPerformances: Boolean,
	val fromFolder: File? = null,
)
