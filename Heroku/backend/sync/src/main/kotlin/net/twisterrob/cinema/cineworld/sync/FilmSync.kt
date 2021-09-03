package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.cinema.database.services.FilmService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject

private val log: Logger = LoggerFactory.getLogger(FilmSync::class.java)

class FilmSync @Inject constructor(
	private val calculator: FilmSyncCalculator,
	private val feedService: FeedService,
	private val dbService: FilmService,
	private val now: () -> OffsetDateTime
) {

	fun sync() {
		val now = now()
		val sync = calculator.calculate(
			now = now,
			feed = feedService.getWeeklyFilmTimes(),
			dbFilms = dbService.findAll()
		)
		log.info(
			"Inserting {} new, updating {} existing ({} restored), deleting {} existing ({} already deleted) {}s for {}.",
			sync.insert.size, sync.update.size, sync.restore.size, sync.delete.size, sync.alreadyDeleted.size,
			"Film",
			now
		)
		log.debug(sync.toString())
		// re `justDeleted`: no actual delete, just added _deleted property
		// re `restored`: no actual insert, just removed _deleted property
		dbService.save(sync.insert + sync.update + sync.delete + sync.restore)
	}
}
