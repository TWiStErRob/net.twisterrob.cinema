package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

private val LOG = LoggerFactory.getLogger(CinemaSyncCalculator::class.java)

class CinemaSyncCalculator @Inject constructor(
	private val nodeSyncer: NodeSyncer<FeedCinema, DBCinema>
) {

	fun calculate(now: OffsetDateTime, feed: Feed, dbCinemas: Iterable<DBCinema>): SyncResults<DBCinema> {
		val feedCinemas = feed.cinemas

		LOG.trace(feedCinemas.toString())
		LOG.trace(dbCinemas.toString())

		val changes = calculateChanges(
			database = dbCinemas,
			databaseIdentity = DBCinema::cineworldID,
			feed = feedCinemas,
			feedIdentity = FeedCinema::id
		)
		return nodeSyncer.update(feed, changes, now)
	}
}
