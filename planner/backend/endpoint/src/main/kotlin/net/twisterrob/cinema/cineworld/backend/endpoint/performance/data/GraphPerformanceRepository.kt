package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import net.twisterrob.cinema.database.services.PerformanceService
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphPerformanceRepository @Inject constructor(
	private val service: PerformanceService,
	private val mapper: PerformanceMapper,
) : PerformanceRepository {

	override fun list(date: LocalDate, films: List<Long>, cinemas: List<Long>): List<Performances> =
		mapper.map(date, service.getPerformances(date = date, cinemaIDs = cinemas, filmEDIs = films).toList())
}
