package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import java.time.LocalDate

interface PerformanceRepository {

	fun list(date: LocalDate, films: List<Long>, cinemas: List<Long>): List<Performances>
}
