package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.cinema.database.services.CinemaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

private val log: Logger = LoggerFactory.getLogger(CinemaSync::class.java)

class CinemaSync @Inject constructor(
	private val feedService: FeedService,
	private val dbService: CinemaService,
	private val nodeSyncer: NodeSyncer<FeedCinema, DBCinema>
) {

	fun sync() {
		val feed = feedService.getWeeklyFilmTimes()
		val feedCinemas = feed.cinemas

		val dbCinemas = dbService.findAll()

		log.trace(feedCinemas.toString())
		log.trace(dbCinemas.toString())

		val changes = calculateChanges(
			dbCinemas, DBCinema::cineworldID,
			feedCinemas, FeedCinema::id
		)
		val now = OffsetDateTime.now()!!
		val sync = nodeSyncer.update(changes, now)
		log.info(
			"Inserting {} new, updating {} existing, deleting {} existing ({} already deleted) {}s for {}.",
			sync.insert.size, sync.update.size, sync.delete.size, sync.alreadyDeleted.size, "Cinema", now
		)
		log.debug(sync.toString())
		// re `justDeleted`: no actual delete, just added _deleted property
		dbService.save(sync.insert + sync.update + sync.delete)
	}
}
