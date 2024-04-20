package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class Cinema(
	@get:JsonProperty("cineworldID")
	val cineworldID: Long,

	@get:JsonProperty("name")
	val name: String,

	@get:JsonProperty("postcode")
	val postcode: String,

	@get:JsonProperty("address")
	val address: String,

	@get:JsonProperty("telephone")
	val telephone: String?,

	@get:JsonProperty("cinema_url")
	val cinemaUrl: String,

	@get:JsonProperty("_created")
	val created: OffsetDateTime,

	@get:JsonProperty("_updated")
	val updated: OffsetDateTime?,

	@get:JsonProperty("class")
	val className: String = "Cinema",

	@get:JsonProperty("fav")
	val isFavorited: Boolean = false,
)
