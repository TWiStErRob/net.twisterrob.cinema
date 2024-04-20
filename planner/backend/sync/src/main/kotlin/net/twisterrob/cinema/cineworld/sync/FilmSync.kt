package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.services.FilmService
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import javax.inject.Inject

private val LOG = LoggerFactory.getLogger(FilmSync::class.java)

class FilmSync @Inject constructor(
	private val calculator: FilmSyncCalculator,
	private val dbService: FilmService,
	private val now: () -> OffsetDateTime
) {

	fun sync(feed: Feed) {
		val now = now()
		val sync = calculator.calculate(
			now = now,
			feed = feed,
			dbFilms = dbService.findAll()
		)
		LOG.info(
			"Inserting {} new, updating {} existing ({} restored), deleting {} existing ({} already deleted) {}s for {}.",
			sync.insert.size, sync.update.size, sync.restore.size, sync.delete.size, sync.alreadyDeleted.size,
			"Film",
			now
		)
		LOG.debug(sync.toString())
		// re `justDeleted`: no actual delete, just added _deleted property
		// re `restored`: no actual insert, just removed _deleted property
		dbService.save(sync.insert + sync.update + sync.delete + sync.restore)
	}
}
