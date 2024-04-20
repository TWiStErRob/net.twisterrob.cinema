package net.twisterrob.cinema.cineworld.sync

import java.io.File

class MainParametersParser {

	fun parse(vararg args: String): MainParameters {
		val isSyncCinemas = "cinemas" in args
		val isSyncFilms = "films" in args
		val isSyncPerformances = "performances" in args
		val folder = args.singleOrNull { it.startsWith(ARG_FOLDER) }?.removePrefix(ARG_FOLDER)
		val params = MainParameters(
			isSyncCinemas = isSyncCinemas || isSyncPerformances,
			isSyncFilms = isSyncFilms || isSyncPerformances,
			isSyncPerformances = isSyncPerformances,
			fromFolder = folder?.let { File(it).absoluteFile },
		)
		require(params.fromFolder?.exists() != false) {
			@Suppress("NullableToStringCall") // It's always non-null, because require() ensured it.
			"Folder does not exist: ${params.fromFolder}"
		}
		require(params.isSyncCinemas || params.isSyncFilms || params.isSyncPerformances) {
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
