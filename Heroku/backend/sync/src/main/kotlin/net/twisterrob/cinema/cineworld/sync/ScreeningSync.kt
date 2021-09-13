package net.twisterrob.cinema.cineworld.sync

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Screening
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.cinema.database.services.FilmService
import net.twisterrob.cinema.database.services.ScreeningService
import org.slf4j.LoggerFactory
import javax.inject.Inject

private val LOG = LoggerFactory.getLogger(ScreeningSync::class.java)

class ScreeningSync @Inject constructor(
	private val cinemaService: CinemaService,
	private val filmService: FilmService,
	private val screeningService: ScreeningService,
	private val toEntity: Creator<Feed.Performance, Screening>,
) {

	fun sync(feed: Feed) {
		screeningService.deleteAll()
		val cinemas = cinemaService.getActiveCinemas().associateBy { it.cineworldID }
		val films = filmService.getActiveFilms().associateBy { it.edi }

		val screenings = feed.performances.map {
			val db = it.toEntity(feed)
			db.film = films[it.film.id]!!
			db.cinema = cinemas[it.cinema.id]!!
			return@map db
		}

		screeningService.save(screenings)
	}
}
