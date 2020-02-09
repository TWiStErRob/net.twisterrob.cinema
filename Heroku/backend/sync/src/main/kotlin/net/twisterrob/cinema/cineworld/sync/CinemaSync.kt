package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.cinema.database.services.CinemaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject

private val log: Logger = LoggerFactory.getLogger(CinemaSync::class.java)

class CinemaSync @Inject constructor(
	private val calculator: CinemaSyncCalculator,
	private val feedService: FeedService,
	private val dbService: CinemaService,
	private val now: () -> OffsetDateTime
) {

	fun sync() {
		val sync = calculator.calculate(
			now = now(),
			feed = feedService.getWeeklyFilmTimes(),
			dbCinemas = dbService.findAll()
		)
		log.info(
			"Inserting {} new, updating {} existing, deleting {} existing ({} already deleted) {}s for {}.",
			sync.insert.size, sync.update.size, sync.delete.size, sync.alreadyDeleted.size, "Cinema", now
		)
		log.debug(sync.toString())
		// re `justDeleted`: no actual delete, just added _deleted property
		dbService.save(sync.insert + sync.update + sync.delete)
	}
}
