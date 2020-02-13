package net.twisterrob.cinema.cineworld.sync

fun main(args: Array<String>) {
	val dagger = DaggerSyncAppComponent
		.builder()
		.graphDBUri(/* default argument */)
		.build()
	dagger.cinemaSync.sync()
}
