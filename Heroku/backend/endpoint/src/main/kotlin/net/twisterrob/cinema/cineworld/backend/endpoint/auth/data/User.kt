package net.twisterrob.cinema.cineworld.backend.endpoint.auth.data

import java.time.OffsetDateTime

data class User(
	val id: String,
	val name: String,
	val email: String,
	val realm: String,
	val _created: OffsetDateTime
)
