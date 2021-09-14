package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import net.twisterrob.cinema.cineworld.quickbook.QuickbookFilm
import net.twisterrob.cinema.cineworld.quickbook.QuickbookFilmSimple
import net.twisterrob.cinema.cineworld.quickbook.QuickbookPerformance
import net.twisterrob.cinema.cineworld.quickbook.QuickbookService
import java.time.LocalDate
import javax.inject.Inject
import net.twisterrob.cinema.database.services.QuickbookService as GraphService

class QuickbookServiceGraph @Inject constructor(
	private val service: GraphService,
) : QuickbookService {

	override fun films(date: LocalDate, cinemas: List<Long>, full: Boolean): List<QuickbookFilm> =
		service.getFilmEDIs(date, cinemas)
			.map { edi ->
				QuickbookFilmSimple(
					edi = edi,
					title = edi.toString()
				)
			}

	override fun performances(date: LocalDate, cinema: Long, film: Long): List<QuickbookPerformance> =
		service.getScreenings(date, cinema, film)
			.map { screening ->
				QuickbookPerformance(
					time = screening.time.toLocalTime(),
					available = true,
					booking_url = screening.booking_url,
					type = "reg",
					ad = false,
					ss = false,
					subtitled = false,
				)
			}
}
