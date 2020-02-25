package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphPerformanceRepository @Inject constructor(
) : PerformanceRepository {

	override fun list(date: LocalDate, films: List<Long>, cinemas: List<Long>): List<Performances> = emptyList()
}
