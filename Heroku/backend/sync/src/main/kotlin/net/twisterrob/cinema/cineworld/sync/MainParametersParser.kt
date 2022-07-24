package net.twisterrob.cinema.cineworld.sync

import java.io.File

class MainParametersParser {

	fun parse(vararg args: String): MainParameters {
		val syncCinemas = "cinemas" in args
		val syncFilms = "films" in args
		val syncPerformances = "performances" in args
		val folder = args.singleOrNull { it.startsWith(ARG_FOLDER) }?.removePrefix(ARG_FOLDER)
		val params = MainParameters(
			syncCinemas = syncCinemas || syncPerformances,
			syncFilms = syncFilms || syncPerformances,
			syncPerformances = syncPerformances,
			fromFolder = folder?.let { File(it).absoluteFile },
		)
		require(params.fromFolder?.exists() != false) {
			"Folder does not exist: ${params.fromFolder}"
		}
		require(params.syncCinemas || params.syncFilms || params.syncPerformances) {
			"""Must have "cinemas" or "films" or "performances" or any/all as an argument."""
		}
		val unprocessed = args.toList()
			.filterNot { it.startsWith(ARG_FOLDER) }
			.filterNot { it == "cinemas" || it == "films" || it == "performances" }
		require(unprocessed.isEmpty()) {
			"""Unknown parameters were passed to sync: ${unprocessed} in ${args}."""
		}
		return params
	}

	private companion object {

		private const val ARG_FOLDER = "--folder="
	}
}
