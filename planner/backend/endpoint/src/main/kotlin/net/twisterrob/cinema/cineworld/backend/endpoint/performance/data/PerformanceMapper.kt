package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.Performance
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset
import javax.inject.Inject

class PerformanceMapper @Inject constructor() {

	fun map(date: LocalDate, performances: List<Performance>): List<Performances> =
		performances
			.groupBy { it.inCinema to it.screensFilm }
			.map { (key, performances) ->
				val (cinema, film) = key
				map(date = date, cinema = cinema, film = film, performances = performances)
			}

	private fun map(date: LocalDate, cinema: Cinema, film: Film, performances: List<Performance>): Performances =
		Performances(
			date = date.atTime(OffsetTime.of(LocalTime.MIDNIGHT, ZoneOffset.UTC)),
			cinema = cinema.cineworldID,
			film = film.edi,
			performances = performances.map { performance ->
				Performances.Performance(
					time = performance.time.toOffsetDateTime(),
					isAvailable = true,
					bookingUrl = performance.booking_url,
					type = "reg",
					isAudioDescribed = false,
					isSuperScreen = false,
					isSubtitled = false
				)
			}
		)
}
