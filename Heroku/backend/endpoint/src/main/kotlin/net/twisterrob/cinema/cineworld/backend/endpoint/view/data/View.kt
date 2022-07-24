package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film

data class View(
	val date: Long,
	val film: Film,
	val cinema: Cinema,
	val user: User
)
