package net.twisterrob.cinema.cineworld.sync

class MainParametersParser {

	fun parse(vararg args: String): MainParameters {
		val syncCinemas = "cinemas" in args
		val syncFilms = "films" in args
		val syncScreenings = "screenings" in args
		val params = MainParameters(
			syncCinemas = syncCinemas || syncScreenings,
			syncFilms = syncFilms || syncScreenings,
			syncScreenings = syncScreenings
		)
		require(params.syncCinemas || params.syncFilms || params.syncScreenings) {
			"""Must have "cinemas" or "films" or "screenings" or any/all as an argument."""
		}
		return params
	}
}
