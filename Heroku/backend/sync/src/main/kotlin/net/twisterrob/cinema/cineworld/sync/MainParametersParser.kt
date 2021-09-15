package net.twisterrob.cinema.cineworld.sync

class MainParametersParser {

	fun parse(vararg args: String): MainParameters {
		val syncCinemas = "cinemas" in args
		val syncFilms = "films" in args
		val syncPerformances = "performances" in args
		val params = MainParameters(
			syncCinemas = syncCinemas || syncPerformances,
			syncFilms = syncFilms || syncPerformances,
			syncPerformances = syncPerformances
		)
		require(params.syncCinemas || params.syncFilms || params.syncPerformances) {
			"""Must have "cinemas" or "films" or "performances" or any/all as an argument."""
		}
		return params
	}
}
