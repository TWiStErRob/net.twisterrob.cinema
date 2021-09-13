package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.services.CinemaService
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject

private val LOG = LoggerFactory.getLogger(CinemaSync::class.java)

class CinemaSync @Inject constructor(
	private val calculator: CinemaSyncCalculator,
	private val dbService: CinemaService,
	private val now: () -> OffsetDateTime
) {

	fun sync(feed: Feed) {
		val now = now()
		val sync = calculator.calculate(
			now = now,
			feed = feed,
			dbCinemas = dbService.findAll()
		)
		LOG.info(
			"Inserting {} new, updating {} existing ({} restored), deleting {} existing ({} already deleted) {}s for {}.",
			sync.insert.size, sync.update.size, sync.restore.size, sync.delete.size, sync.alreadyDeleted.size,
			"Cinema",
			now
		)
		LOG.debug(sync.toString())
		dbService.save(sync.insert + sync.update + sync.delete + sync.restore)
	}
}
