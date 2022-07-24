package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.cinema.database.services.FilmService
import net.twisterrob.cinema.database.services.PerformanceService
import org.slf4j.LoggerFactory
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Performance as FeedPerformance
import net.twisterrob.cinema.database.model.Performance as DBPerformance

private val LOG = LoggerFactory.getLogger(PerformanceSync::class.java)

class PerformanceSync @Inject constructor(
	private val cinemaService: CinemaService,
	private val filmService: FilmService,
	private val performanceService: PerformanceService,
	private val toEntity: Creator<FeedPerformance, DBPerformance>,
) {

	fun sync(feed: Feed) {
		val cinemas = cinemaService.getActiveCinemas().associateBy { it.cineworldID }
		val films = filmService.getActiveFilms().associateBy { it.edi }

		val performances = feed.performances.map { performance ->
			performance.toEntity(feed).apply {
				this.screensFilm = films[performance.film.id]!!
				this.inCinema = cinemas[performance.cinema.id]!!
			}
		}

		val deleted = performanceService.deleteAll()
		LOG.info("Deleted {}, inserting {} new {}s.", deleted, performances.size, "Performance")
		performanceService.save(performances)
	}
}
