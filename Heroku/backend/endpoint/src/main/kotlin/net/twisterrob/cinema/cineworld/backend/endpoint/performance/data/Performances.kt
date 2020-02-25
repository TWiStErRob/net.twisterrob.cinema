package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film
import java.time.LocalDate

data class Performances(
	val date: LocalDate,
	val cinema: Cinema,
	val film: Film,
	val performances: List<Performance>
)
