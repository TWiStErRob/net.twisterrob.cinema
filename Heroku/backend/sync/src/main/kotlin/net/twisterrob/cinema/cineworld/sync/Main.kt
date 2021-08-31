package net.twisterrob.cinema.cineworld.sync

fun main() {
	val dagger = DaggerSyncAppComponent
		.builder()
		.graphDBUri(/* default argument */)
		.build()
	dagger.cinemaSync.sync()
	dagger.filmSync.sync()
}
