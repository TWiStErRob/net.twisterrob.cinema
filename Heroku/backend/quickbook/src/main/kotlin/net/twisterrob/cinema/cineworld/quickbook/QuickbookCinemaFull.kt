package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

class QuickbookCinemaFull(
	/**
	 * @sample "Queens Links Leisure Park, Links Road, Aberdeen"
	 */
	@JsonProperty("address")
	val address: String,

	/**
	 * @sample "http://www.cineworld.co.uk/cinemas/1/information"
	 */
	@JsonProperty("cinema_url")
	val cinemaUrl: URI,

	/**
	 * @sample 1
	 */
	@JsonProperty("id")
	override val id: Int,

	/**
	 * @sample "Aberdeen - Queens Links"
	 */
	@JsonProperty("name")
	override val name: String,

	/**
	 * @sample "AB24 5EN"
	 */
	@JsonProperty("postcode")
	val postcode: String,

	/**
	 * @sample "0871 200 2000"
	 */
	@JsonProperty("telephone")
	val telephone: String,
) : QuickbookCinema
