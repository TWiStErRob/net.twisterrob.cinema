package net.twisterrob.cinema.cineworld.quickbook

import java.time.LocalDate

interface QuickbookService {

	fun films(full: Boolean = false): List<QuickbookFilm>

	fun films(date: LocalDate, cinemas: List<Long>, full: Boolean = false): List<QuickbookFilm>

	fun performances(date: LocalDate, cinema: Long, film: Long): List<QuickbookPerformance>
}
