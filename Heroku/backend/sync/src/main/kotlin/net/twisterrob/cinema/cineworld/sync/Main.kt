package net.twisterrob.cinema.cineworld.sync

import java.net.URI

fun main(vararg args: String) {
	val params = MainParametersParser().parse(*args)
		.also { println("Syncing: $it") }
	val dagger = DaggerSyncAppComponent
		.builder()
		.graphDBUri(getNeo4jUrl())
		.build()
	try {
		if (params.syncCinemas) {
			dagger.cinemaSync.sync()
		}
		if (params.syncFilms) {
			dagger.filmSync.sync()
		}
	} finally {
		dagger.neo4j.close()
		dagger.network.close()
	}
}

private fun getNeo4jUrl(): URI {
	val url = System.getenv()["NEO4J_URL"]
		?: error("NEO4J_URL environment variable must be defined (=neo4j+s://username:password@hostname:port).")
	return URI.create(url)
}
