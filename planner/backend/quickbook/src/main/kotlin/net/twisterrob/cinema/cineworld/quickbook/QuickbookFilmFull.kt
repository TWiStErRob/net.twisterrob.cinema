package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

data class QuickbookFilmFull(
	/**
	 * @sample 6237
	 */
	@JsonProperty("id")
	val id: Long,

	/**
	 * @sample 63829
	 */
	@JsonProperty("edi")
	override val edi: Long,

	/**
	 * @sample "This Is The End"
	 */
	@JsonProperty("title")
	override val title: String,

	/**
	 * @sample "http://www.cineworld.co.uk/whatson/6237"
	 */
	@JsonProperty("film_url")
	val filmUrl: URI,

	/**
	 * @sample "15"
	 */
	@JsonProperty("classification")
	val classification: String,

	/**
	 * @sample "15"
	 */
	@JsonProperty("advisory")
	val advisory: String?,

	/**
	 * @sample "http://www.cineworld.co.uk/assets/media/films/6237_poster.jpg"
	 */
	@JsonProperty("poster_url")
	val posterUrl: URI,

	/**
	 * @sample "http://www.cineworld.co.uk/assets/media/films/6237_still.jpg"
	 */
	@JsonProperty("still_url")
	val stillUrl: URI?,

	/**
	 * @sample `false`
	 */
	@JsonProperty("3D")
	val is3D: Boolean,

	/**
	 * @sample `false`
	 */
	@JsonProperty("imax")
	val isIMAX: Boolean,
) : QuickbookFilm
