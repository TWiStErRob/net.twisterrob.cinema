package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import com.fasterxml.jackson.annotation.JsonProperty
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import java.net.URI
import java.time.OffsetDateTime

data class Film(
	@get:JsonProperty("cineworldID")
	val cineworldID: Long?,

	@get:JsonProperty("title")
	val title: String,

	@get:JsonProperty("director")
	val director: String,

	@get:JsonProperty("release")
	val release: OffsetDateTime,

	@get:JsonProperty("format")
	val format: String,

	@get:JsonProperty("runtime")
	val runtime: Long,

	@get:JsonProperty("poster_url")
	val posterUrl: URI,

	@get:JsonProperty("cineworldInternalID")
	val cineworldInternalID: Long,

	@get:JsonProperty("cert")
	val cert: String,

	@get:JsonProperty("imax")
	val isIMAX: Boolean,

	@get:JsonProperty("3D")
	val is3D: Boolean,

	@get:JsonProperty("film_url")
	val filmUrl: URI,

	@get:JsonProperty("edi")
	val edi: Long,

	@get:JsonProperty("classification")
	val classification: String,

	@get:JsonProperty("trailer")
	val trailer: URI?,

	@get:JsonProperty("actors")
	val actors: String,

	@get:JsonProperty("originalTitle")
	val originalTitle: String,

	@get:JsonProperty("categories")
	val categories: List<String>,

	@get:JsonProperty("weighted")
	val weighted: Long,

	@get:JsonProperty("slug")
	val slug: String,

	@get:JsonProperty("group")
	val group: Long,

	@get:JsonProperty("_created")
	val created: OffsetDateTime,

	@get:JsonProperty("_updated")
	val updated: OffsetDateTime?,

	@get:JsonProperty("class")
	val className: String = "Film",

	@get:JsonProperty("view")
	val view: View? = null,
)
