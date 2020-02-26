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
		combinations(date, films, cinemas).map {
			val performances =
				quickbookService.performances(date = it.date, cinema = it.cinema, film = it.film)
			performanceMapper.map(date, it.cinema, it.film, performances)
		}

	private fun combinations(date: LocalDate, films: List<Long>, cinemas: List<Long>): List<PerformanceCombination> =
		(cinemas x films).map { (cinema, film) ->
			PerformanceCombination(date = date, cinema = cinema, film = film)
		}

	private data class PerformanceCombination(
		val date: LocalDate,
		val cinema: Long,
		val film: Long
	)
}

private infix fun <T1, T2> Iterable<T1>.x(list2: Iterable<T2>): Iterable<Pair<T1, T2>> =
	flatMap { item1 ->
		list2.map { item2 ->
			item1 to item2
		}
	}
