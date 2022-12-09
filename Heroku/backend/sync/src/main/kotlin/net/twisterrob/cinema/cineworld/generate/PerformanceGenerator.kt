package net.twisterrob.cinema.cineworld.generate

import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.cinema.database.services.FilmService
import java.lang.Math.random
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class PerformanceGenerator @Inject constructor(
	private val cinemaService: CinemaService,
	private val filmService: FilmService,
	private val creator: RandomPerformanceCreator,
) {

	fun generate(): Feed {
		val cinemas = cinemaService.getActiveCinemas()
		val films = filmService.getActiveFilms()
		val feedCinemas = cinemas.map(::mapCinema)
		val feedFilms = films.map(::mapFilm)
		return Feed(
			emptyList(),
			feedCinemas,
			feedFilms,
			generatePerformances(feedCinemas, feedFilms),
		)
	}

	private fun mapCinema(it: Cinema): Feed.Cinema =
		Feed.Cinema(
			id = it.cineworldID,
			url = it.cinema_url,
			name = it.name,
			address = it.address,
			postcode = it.postcode,
			phone = it.telephone,
			services = "",
		)

	private fun mapFilm(it: Film): Feed.Film =
		Feed.Film(
			id = it.edi,
			title = it.title,
			url = it.film_url,
			classification = it.classification,
			releaseDate = it.release.toLocalDate(),
			runningTime = it.runtime.toInt(),
			director = it.director,
			cast = it.actors,
			synopsis = "",
			posterUrl = it.poster_url,
			reasonToSee = null,
			attributes = inferAttributes(it).joinToString(separator = ","),
			trailerUrl = it.trailer,
		)

	private fun inferAttributes(it: Film): List<String> =
		listOfNotNull(
			if (it.is3D) "3D" else null,
			if (it.isIMAX) "IMAX" else null,
			*when (it.format) {
				"IMAX2D" -> arrayOf("IMAX", "2D")
				"IMAX3D" -> arrayOf("IMAX", "3D")
				"IMAX" -> arrayOf("IMAX")
				else -> emptyArray()
			},
		)

	private fun generatePerformances(
		feedCinemas: List<Feed.Cinema>,
		feedFilms: List<Feed.Film>
	): List<Feed.Performance> =
		feedCinemas
			.flatMap { cinema -> feedFilms.map { film -> cinema to film } }
			.filter { random() < FUZZY_THRESHOLD }
			.flatMap { (cinema, film) ->
				val performanceCount = (random() * MAX_PERFORMANCES_IN_CINEMA).toInt()
				(0..performanceCount).map { creator.create(film, cinema) }
			}

	companion object {

		/** Only 70% of the combinations will be populated so not all movies are screened in all cinemas. */
		private const val FUZZY_THRESHOLD = 0.7

		/** It's a realistic value, screenings per movie per day in a specific cinema. */
		private const val MAX_PERFORMANCES_IN_CINEMA = 5
	}
}

class RandomPerformanceCreator @Inject constructor() {

	fun create(film: Feed.Film, cinema: Feed.Cinema): Feed.Performance =
		Feed.Performance(
			film = film,
			cinema = cinema,
			url = URI.create("https://www.cineworld.co.uk"),
			date = randomTime().atOffset(ZoneOffset.UTC),
			attributes = "",
		)

	private fun randomTime(): LocalDateTime =
		LocalDate.now()
			.atStartOfDay() // today
			.plusDays(randBetween(START_DAY_RELATIVE, END_DAY_RELATIVE).toLong())
			.plusHours(randBetween(SCREENING_START_HOUR, SCREENING_END_HOUR).toLong())
			.plusMinutes(@Suppress("MagicNumber") (randBetween(0, 6) * 10).toLong())

	private fun randBetween(start: Int, endExclusive: Int): Int =
		start + (random() * (endExclusive - start)).toInt()

	companion object {

		private const val SCREENING_START_HOUR = 10
		private const val SCREENING_END_HOUR = 21
		private const val START_DAY_RELATIVE = -2
		private const val END_DAY_RELATIVE = +14
	}
}
