package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import com.fasterxml.jackson.annotation.JsonProperty
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film

data class View(
	@get:JsonProperty("date")
	val date: Long,

	@get:JsonProperty("film")
	val film: Film,

	@get:JsonProperty("cinema")
	val cinema: Cinema,

	@get:JsonProperty("user")
	val user: User,
)
