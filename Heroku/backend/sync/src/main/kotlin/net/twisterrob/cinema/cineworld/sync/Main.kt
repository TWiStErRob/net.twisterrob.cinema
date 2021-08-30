package net.twisterrob.cinema.cineworld.sync

fun main() {
	val dagger = DaggerSyncAppComponent
		.builder()
		.graphDBUri(/* default argument */)
		.build()
	try {
		dagger.cinemaSync.sync()
		dagger.filmSync.sync()
	} finally {
		dagger.neo4j.close()
		dagger.network.close()
	}
}
