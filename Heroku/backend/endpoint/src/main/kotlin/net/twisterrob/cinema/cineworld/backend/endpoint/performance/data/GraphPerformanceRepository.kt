package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import net.twisterrob.cinema.cineworld.quickbook.QuickbookService
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphPerformanceRepository @Inject constructor(
	private val quickbookService: QuickbookService,
	private val performanceMapper: PerformanceMapper
) : PerformanceRepository {

	override fun list(date: LocalDate, films: List<Long>, cinemas: List<Long>): List<Performances> =
		(cinemas x films).map { (cinema, film) ->
			// TODO make these network calls in parallel (3)
			val performances =
				quickbookService.performances(date = date, cinema = cinema, film = film)
			performanceMapper.map(date, cinema, film, performances)
		}
}

private infix fun <T1, T2> Iterable<T1>.x(other: Iterable<T2>): Iterable<Pair<T1, T2>> =
	this.flatMap { item1 ->
		other.map { item2 ->
			item1 to item2
		}
	}
