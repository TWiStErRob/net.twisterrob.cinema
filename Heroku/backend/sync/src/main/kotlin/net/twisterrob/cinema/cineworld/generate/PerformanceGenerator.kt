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
		val films = filmService.getAllFilms()
		val feedCinemas = cinemas.map(::mapCinema)
		val feedFilms = films.shuffled().take(ACTIVE_FILM_COUNT).map(::mapFilm)
		return Feed(
			emptyList(),
			feedCinemas,
			feedFilms,
			creator.generatePerformances(feedCinemas, feedFilms),
		)
	}

	private fun mapCinema(cinema: Cinema): Feed.Cinema =
		Feed.Cinema(
			id = cinema.cineworldID,
			url = cinema.cinema_url,
			name = cinema.name,
			address = cinema.address,
			postcode = cinema.postcode,
			phone = cinema.telephone,
			services = "",
		)

	private fun mapFilm(film: Film): Feed.Film =
		Feed.Film(
			id = film.edi,
			title = film.title,
			url = film.film_url,
			classification = film.classification,
			releaseDate = film.release.toLocalDate(),
			runningTime = film.runtime.toInt(),
			director = film.director,
			cast = film.actors,
			synopsis = "",
			posterUrl = film.poster_url,
			reasonToSee = null,
			attributes = inferAttributes(film).joinToString(separator = ","),
			trailerUrl = film.trailer,
		)

	/** @see net.twisterrob.cinema.cineworld.sync.copyPropertiesFrom */
	private fun inferAttributes(film: Film): List<String> =
		listOfNotNull(
			if (film.is3D) "3D" else null,
			if (film.isIMAX) "IMAX" else null,
			// See net.twisterrob.cinema.cineworld.sync.findFormat
			*when (film.format) {
				"IMAX2D" -> arrayOf("IMAX", "2D")
				"IMAX3D" -> arrayOf("IMAX", "3D")
				"IMAX" -> arrayOf("IMAX")
				else -> emptyArray()
			},
			// See net.twisterrob.cinema.cineworld.sync.formatTitle
			*parseAttributesFromTitle(film.title).toTypedArray()
		).distinct().sorted()

	private fun parseAttributesFromTitle(title: String): List<String> =
		@Suppress("RegExpRedundantEscape")
		Regex("""^.*? \[(.*)\]$""")
			.find(title)
			?.let { it.groupValues[1] }
			?.split(", ")
			.orEmpty()

	companion object {

		private const val ACTIVE_FILM_COUNT = 20
	}
}

class RandomPerformanceCreator @Inject constructor() {

	fun generatePerformances(feedCinemas: List<Feed.Cinema>, feedFilms: List<Feed.Film>): List<Feed.Performance> {
		val dates = relativeDates(START_DAY_RELATIVE, END_DAY_RELATIVE)
		return feedCinemas
			.flatMap { cinema ->
				feedFilms.flatMap { film ->
					dates.map { date ->
						Triple(cinema, film, date)
					}
				}
			}
			.filter { random() < FUZZY_THRESHOLD }
			.flatMap { (cinema, film, date) ->
				val maxPerformanceCount =
					if (cinema.name.startsWith("London - "))
						MAX_PERFORMANCES_IN_LONDON_CINEMA
					else
						MAX_PERFORMANCES_IN_RURAL_CINEMA
				val performanceCount = (random() * maxPerformanceCount).toInt()
				(0..performanceCount).map { create(cinema, film, date) }
			}
	}

	private fun relativeDates(start: Int, endInclusive: Int): List<LocalDate> =
		(start..endInclusive)
			.map { LocalDate.now().plusDays(it.toLong()) }

	private fun create(cinema: Feed.Cinema, film: Feed.Film, date: LocalDate): Feed.Performance =
		Feed.Performance(
			film = film,
			cinema = cinema,
			url = URI.create("https://www.cineworld.co.uk"),
			date = randomTime(date).atOffset(ZoneOffset.UTC),
			attributes = "",
		)

	private fun randomTime(date: LocalDate): LocalDateTime =
		date
			.atStartOfDay()
			.plusHours(randBetween(SCREENING_START_HOUR, SCREENING_END_HOUR).toLong())
			.plusMinutes(@Suppress("MagicNumber") (randBetween(0, 6) * 10).toLong())

	private fun randBetween(start: Int, endExclusive: Int): Int =
		start + (random() * (endExclusive - start)).toInt()

	companion object {

		/** Only 60% of the combinations will be populated so not all movies are screened in all cinemas. */
		private const val FUZZY_THRESHOLD = 0.6

		/** It's a realistic value, screenings per movie per day in a specific cinema. */
		private const val MAX_PERFORMANCES_IN_LONDON_CINEMA = 5
		private const val MAX_PERFORMANCES_IN_RURAL_CINEMA = 2

		private const val SCREENING_START_HOUR = 10
		private const val SCREENING_END_HOUR = 21
		private const val START_DAY_RELATIVE = -1
		private const val END_DAY_RELATIVE = +7
	}
}
