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
			.groupBy { Pair(it.inCinema, it.screensFilm) }
			.map { (key, performances) ->
				val (cinema, film) = key
				map(date, cinema, film, performances)
			}

	private fun map(date: LocalDate, cinema: Cinema, film: Film, performances: List<Performance>): Performances =
		Performances(
			date = date.atTime(OffsetTime.of(LocalTime.MIDNIGHT, ZoneOffset.UTC)),
			cinema = cinema.cineworldID,
			film = film.edi,
			performances = performances.map {
				Performances.Performance(
					time = it.time.toOffsetDateTime(),
					available = true,
					booking_url = it.booking_url,
					type = "reg",
					ad = false,
					ss = false,
					subtitled = false
				)
			}
		)
}
