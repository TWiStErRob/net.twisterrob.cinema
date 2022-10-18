package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import com.fasterxml.jackson.annotation.JsonProperty

data class CurrentUser(
	@get:JsonProperty("id")
	val id: String,

	@get:JsonProperty("email")
	val email: String,
)
