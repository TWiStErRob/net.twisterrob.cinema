package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Film as FeedFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

private val LOG = LoggerFactory.getLogger(FilmSyncCalculator::class.java)

class FilmSyncCalculator @Inject constructor(
	private val nodeSyncer: NodeSyncer<FeedFilm, DBFilm>
) {

	fun calculate(now: OffsetDateTime, feed: Feed, dbFilms: Iterable<DBFilm>): SyncResults<DBFilm> {
		val feedFilms = feed.films

		LOG.trace(feedFilms.toString())
		LOG.trace(dbFilms.toString())

		val changes = calculateChanges(
			database = dbFilms,
			databaseIdentity = DBFilm::edi,
			feed = feedFilms,
			feedIdentity = FeedFilm::id
		)
		return nodeSyncer.update(feed, changes, now)
	}
}
