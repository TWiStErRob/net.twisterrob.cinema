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
import net.twisterrob.cinema.database.model.Screening
import net.twisterrob.cinema.database.services.Services
import net.twisterrob.ktor.client.configureLogging
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Singleton

@Component(modules = [Neo4JModule::class, SyncAppModule::class])
@Singleton
@Neo4J
interface SyncAppComponent : Services {

	val main: Main

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): SyncAppComponent
	}
}

@Module(includes = [SyncAppModule.Bindings::class])
class SyncAppModule {

	@Provides
	fun cinemaEntityFactory(updater: Updater<Cinema, Feed.Cinema>): Creator<Feed.Cinema, Cinema> =
		fun Feed.Cinema.(feed: Feed): Cinema =
			Cinema().also { updater(it, this, feed) }

	@Provides
	fun filmEntityFactory(updater: Updater<Film, Feed.Film>): Creator<Feed.Film, Film> =
		fun Feed.Film.(feed: Feed): Film =
			Film().also { updater(it, this, feed) }

	@Provides
	fun screeningEntityFactory(updater: Updater<Screening, Feed.Performance>): Creator<Feed.Performance, Screening> =
		fun Feed.Performance.(feed: Feed): Screening =
			Screening().also { updater(it, this, feed) }

	@Provides
	fun nowProvider(): () -> OffsetDateTime =
		{ OffsetDateTime.now() }

	@Provides
	fun copyCinemaProperties(): Updater<Cinema, Feed.Cinema> =
		Cinema::copyPropertiesFrom

	@Provides
	fun copyFilmProperties(): Updater<Film, Feed.Film> =
		Film::copyPropertiesFrom

	@Provides
	fun copyScreeningProperties(): Updater<Screening, Feed.Performance> =
		Screening::copyPropertiesFrom

	@Provides
	fun httpClient(): HttpClient =
		HttpClient().config {
			configureLogging(LoggerFactory.getLogger(HttpClient::class.java))
		}

	@Module
	interface Bindings {

		@Binds
		fun bindFeedService(impl: FeedServiceNetwork): FeedService
	}
}
