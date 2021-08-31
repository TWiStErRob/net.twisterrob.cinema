package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

private val log: Logger = LoggerFactory.getLogger(CinemaSync::class.java)

class CinemaSyncCalculator @Inject constructor(
	private val nodeSyncer: NodeSyncer<FeedCinema, DBCinema>
) {

	fun calculate(now: OffsetDateTime, feed: Feed, dbCinemas: Iterable<DBCinema>): SyncResults<DBCinema> {
		val feedCinemas = feed.cinemas

		log.trace(feedCinemas.toString())
		log.trace(dbCinemas.toString())

		val changes = calculateChanges(
			dbCinemas, DBCinema::cineworldID,
			feedCinemas, FeedCinema::id
		)
		return nodeSyncer.update(feed, changes, now)
	}
}
