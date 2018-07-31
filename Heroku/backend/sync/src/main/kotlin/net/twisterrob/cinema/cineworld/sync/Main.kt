package net.twisterrob.cinema.cineworld.sync

fun main(args: Array<String>) {
	val dagger = DaggerSyncAppComponent.create()!!
	dagger.filmSync.sync()
}
