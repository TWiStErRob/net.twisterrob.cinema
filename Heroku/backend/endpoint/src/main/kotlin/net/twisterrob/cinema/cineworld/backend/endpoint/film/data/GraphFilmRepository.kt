package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import net.twisterrob.cinema.database.services.FilmService
import java.time.LocalDate
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm

class GraphFilmRepository @Inject constructor(
	private val service: FilmService,
	private val mapper: FilmMapper,
) : FilmRepository {

	override fun getFilms(date: LocalDate, cinemas: List<Long>): List<FrontendFilm> {
		val result = service.getFilms(date, cinemas)
		return result.map(mapper::map)
	}

	override fun getFilmsAuth(userId: String, date: LocalDate, cinemas: List<Long>): List<FrontendFilm> {
		val result = service.getFilmsAuth(date, cinemas, userId)
		return result.map(mapper::map)
	}

	override fun getFilm(edi: Long): FrontendFilm? =
		service.getFilm(edi)?.let { mapper.map(it) }
}
