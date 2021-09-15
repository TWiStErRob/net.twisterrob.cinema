package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import net.twisterrob.cinema.cineworld.quickbook.QuickbookService
import net.twisterrob.cinema.database.services.FilmService
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm

@Singleton
class QuickbookFilmRepository @Inject constructor(
	private val service: FilmService,
	private val mapper: FilmMapper,
	private val quickbook: QuickbookService,
) : FilmRepository {

	override fun getFilms(date: LocalDate, cinemas: List<Long>): List<FrontendFilm> {
		val edis = quickbook.getFilmEDIs(date, cinemas)
		if (edis.isEmpty()) return emptyList()
		val result = service.getFilms(edis)
		return result.map(mapper::map)
	}

	override fun getFilmsAuth(userId: String, date: LocalDate, cinemas: List<Long>): List<FrontendFilm> {
		val edis = quickbook.getFilmEDIs(date, cinemas)
		if (edis.isEmpty()) return emptyList()
		val result = service.getFilmsAuth(edis, userId)
		return result.map(mapper::map)
	}

	override fun getFilm(edi: Long): FrontendFilm? =
		service.getFilm(edi)?.let { mapper.map(it) }
}

private fun QuickbookService.getFilmEDIs(date: LocalDate, cinemas: List<Long>): List<Long> {
	if (cinemas.isEmpty()) {
		return emptyList()
	}
	val films = this.films(date = date, cinemas = cinemas)
	return films.map { it.edi }
}
