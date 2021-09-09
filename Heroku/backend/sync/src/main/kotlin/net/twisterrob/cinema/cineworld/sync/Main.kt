package net.twisterrob.cinema.cineworld.sync

import java.net.URI

fun main() {
	val dagger = DaggerSyncAppComponent
		.builder()
		.graphDBUri(getNeo4jUrl())
		.build()
	try {
		dagger.cinemaSync.sync()
		dagger.filmSync.sync()
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
