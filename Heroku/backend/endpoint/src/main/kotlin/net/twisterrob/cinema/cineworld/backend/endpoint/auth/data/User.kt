package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class User(
	@get:JsonProperty("id")
	val id: String,

	@get:JsonProperty("name")
	val name: String,

	@get:JsonProperty("email")
	val email: String,

	@get:JsonProperty("realm")
	val realm: String,

	@get:JsonProperty("_created")
	val created: OffsetDateTime,
)
