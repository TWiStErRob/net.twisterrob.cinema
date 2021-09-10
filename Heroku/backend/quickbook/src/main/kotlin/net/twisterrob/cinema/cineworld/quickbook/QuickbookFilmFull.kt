package net.twisterrob.cinema.cineworld.quickbook

import java.net.URI

data class QuickbookFilmFull(
	/**
	 * @sample 6237
	 */
	val id: Long,

	/**
	 * @sample 63829
	 */
	override val edi: Long,

	/**
	 * @sample "This Is The End"
	 */
	override val title: String,

	/**
	 * @sample "http://www.cineworld.co.uk/whatson/6237"
	 */
	val film_url: URI,

	/**
	 * @sample "15"
	 */
	val classification: String,

	/**
	 * @sample "15"
	 */
	val advisory: String?,

	/**
	 * @sample "http://www.cineworld.co.uk/assets/media/films/6237_poster.jpg"
	 */
	val poster_url: URI,

	/**
	 * @sample "http://www.cineworld.co.uk/assets/media/films/6237_still.jpg"
	 */
	val still_url: URI?,

	/**
	 * @sample `false`
	 */
	val `3D`: Boolean,

	/**
	 * @sample `false`
	 */
	val imax: Boolean
) : QuickbookFilm
