package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import java.time.LocalDate

interface FilmRepository {

	fun getFilms(date: LocalDate, cinemas: List<Long>): List<Film>

	fun getFilmsAuth(userId: String, date: LocalDate, cinemas: List<Long>): List<Film>

	fun getFilm(edi: Long): Film?
}
