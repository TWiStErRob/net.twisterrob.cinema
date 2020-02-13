package net.twisterrob.cinema.cineworld.sync

import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.cinema.cineworld.sync.syndication.FeedServiceNetwork
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.services.CinemaServices
import java.time.OffsetDateTime
import javax.inject.Singleton

@Component(modules = [Neo4JModule::class, SyncAppModule::class])
@Singleton
@Neo4J
interface SyncAppComponent : CinemaServices {

	val cinemaSync: CinemaSync
	val filmSync: FilmSync

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): SyncAppComponent
	}
}

@Module(includes = [SyncAppModule.Bindings::class])
class SyncAppModule {

	@Provides
	fun cinemaEntityFactory(updater: Updater<Cinema, Feed.Cinema>): Creator<Feed.Cinema, Cinema> =
		fun Feed.Cinema.(): Cinema =
			Cinema().also { updater(it, this) }

	@Provides
	fun filmEntityFactory(updater: Updater<Film, Feed.Film>): Creator<Feed.Film, Film> =
		fun Feed.Film.(): Film =
			Film().also { updater(it, this) }

	@Provides
	fun nowProvider(): () -> OffsetDateTime =
		{ OffsetDateTime.now() }

	@Provides
	fun copyCinemaProperties(): Updater<Cinema, Feed.Cinema> =
		fun Cinema.(feed) {
			this.cineworldID = feed.id
			this.name = feed.name.replace("""^Cineworld """.toRegex(), "")
			this.postcode = feed.postcode
			this.address = feed.address
			this.telephone = feed.phone
			this.cinema_url = feed.url
			// TODO feed.serviceList
		}

	@Provides
	fun copyFilmProperties(): Updater<Film, Feed.Film> =
		fun Film.(feed) {
			this.edi = feed.id
			this.title = feed.title
			this.director = feed.director
			this.film_url = feed.url
			this.poster_url = feed.posterUrl
			this.runtime = feed.runningTime.toLong()
			this.trailer = feed.trailerUrl
		}

	@Provides
	fun httpClient(): HttpClient = HttpClient()

	@Module
	interface Bindings {

		@Binds
		fun bindFeedService(impl: FeedServiceNetwork): FeedService
	}
}
