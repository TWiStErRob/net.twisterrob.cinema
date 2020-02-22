package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import net.twisterrob.cinema.database.services.FilmService
import javax.inject.Inject
import javax.inject.Singleton
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm

@Singleton
class GraphFilmRepository @Inject constructor(
	private val service: FilmService,
	private val mapper: FilmMapper
) : FilmRepository {

	override fun getFilm(edi: Long): FrontendFilm? =
		service.getFilm(edi)?.let { mapper.map(it) }
}
