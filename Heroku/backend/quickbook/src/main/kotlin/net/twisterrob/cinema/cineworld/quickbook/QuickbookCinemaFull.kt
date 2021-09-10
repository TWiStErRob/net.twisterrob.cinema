package net.twisterrob.cinema.cineworld.quickbook

import java.net.URI

class QuickbookCinemaFull(
	/**
	 * @sample "Queens Links Leisure Park, Links Road, Aberdeen"
	 */
	val address: String,

	/**
	 * @sample "http://www.cineworld.co.uk/cinemas/1/information"
	 */
	val cinema_url: URI,

	/**
	 * @sample 1
	 */
	override val id: Int,

	/**
	 * @sample "Aberdeen - Queens Links"
	 */
	override val name: String,

	/**
	 * @sample "AB24 5EN"
	 */
	val postcode: String,

	/**
	 * @sample "0871 200 2000"
	 */
	val telephone: String,
) : QuickbookCinema
