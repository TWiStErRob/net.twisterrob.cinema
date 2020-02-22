package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

/**
 * Repository that will handle operations related to the films.
 */
interface FilmRepository {

	fun getFilm(edi: Long): Film?
}
