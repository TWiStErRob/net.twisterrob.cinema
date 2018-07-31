package net.twisterrob.cinema.cineworld.sync

import dagger.Component
import dagger.Module
import dagger.Provides
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.services.CinemaServices
import javax.inject.Singleton

@Component(modules = [Neo4JModule::class, SyncAppModule::class])
@Singleton
@Neo4J
interface SyncAppComponent : CinemaServices {

	val cinemaSync: CinemaSync
	val filmSync: FilmSync
}

@Module
class SyncAppModule {

	@Provides
	fun cinemaEntityFactory(): Creator<Feed.Cinema, Cinema> = fun(_): Cinema = Cinema()

	@Provides
	fun filmEntityFactory(): Creator<Feed.Film, Film> = fun(_): Film = Film()

	@Provides
	fun copyCinemaProperties(): Updater<Cinema, Feed.Cinema> = fun(db, feed) {
		db.cineworldID = feed.id
		db.name = feed.name.replace("""^Cineworld """.toRegex(), "")
		db.postcode = feed.postcode
		db.address = feed.address
		db.telephone = feed.phone
		db.cinema_url = feed.url.toString()
		// TODO feed.serviceList
	}

	@Provides
	fun copyFilmProperties(): Updater<Film, Feed.Film> = fun(db, feed) {
		db.edi = feed.id
		db.title = feed.title
		db.director = feed.director
		db.film_url = feed.url.toString()
		db.poster_url = feed.posterUrl.toString()
		db.runtime = feed.runningTime.toLong()
		db.trailer = feed.trailerUrl?.toString()
	}
}
