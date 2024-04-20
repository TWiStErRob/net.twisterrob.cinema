package net.twisterrob.cinema.cineworld.sync

import io.ktor.client.HttpClient
import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.net.URI
import javax.inject.Inject

private val LOG = LoggerFactory.getLogger(Main::class.java)

class Main @Inject constructor(
	private val feedService: FeedService,
	private val cinemaSync: CinemaSync,
	private val filmSync: FilmSync,
	private val performanceSync: PerformanceSync,
	private val neo4j: SessionFactory,
	private val network: HttpClient,
) : Closeable {

	fun sync(params: MainParameters) {
		LOG.info("Syncing: $params")
		val feed by lazy { feedService.getWeeklyFilmTimes() }
		if (params.isSyncCinemas) {
			cinemaSync.sync(feed)
		}
		if (params.isSyncFilms) {
			filmSync.sync(feed)
		}
		if (params.isSyncPerformances) {
			performanceSync.sync(feed)
		}
	}

	override fun close() {
		neo4j.close()
		network.close()
	}

	companion object {

		@Suppress("MemberNameEqualsClassName")
		@JvmStatic
		fun main(vararg args: String) {
			val params = MainParametersParser().parse(*args)
			val dagger = DaggerSyncAppComponent
				.builder()
				.graphDBUri(getNeo4jUrl())
				.params(params)
				.build()
			dagger.main.use { it.sync(params) }
		}

		private fun getNeo4jUrl(): URI {
			val url = System.getenv()["NEO4J_URL"]
				?: error("NEO4J_URL environment variable must be defined (=neo4j+s://username:password@hostname:port).")
			return URI.create(url)
		}
	}
}
