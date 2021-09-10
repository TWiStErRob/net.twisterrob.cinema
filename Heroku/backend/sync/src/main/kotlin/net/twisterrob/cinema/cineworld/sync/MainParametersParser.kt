package net.twisterrob.cinema.cineworld.sync

class MainParametersParser {

	fun parse(vararg args: String): MainParameters {
		val params = MainParameters(
			syncCinemas = "cinemas" in args,
			syncFilms = "films" in args,
		)
		require(params.syncCinemas || params.syncFilms) {
			"""Must have "cinemas" or "films" or both as an argument."""
		}
		return params
	}
}
