@file:Suppress("unused")

package net.twisterrob.cinema.cineworld.quickbook

import java.net.URI

/**
 * Response to `https://www.cineworld.co.uk/api/film/list`.
 *
 * To transfer closer to [QuickbookFilmFull].
 * ```
 * response.base_url = response.base_url || 'https://www.cineworld.co.uk/';
 * response.films.forEach { film ->
 *     film.cineworldInternalID = film.id; delete film.id;
 *     film.poster_url = response.base_url + film.poster; delete film.poster;
 *     film.release = moment(film.release, 'YYYYMMDD').format();
 *     film.runtime = film.length; delete film.length;
 *     if (film.categories !== null && film.categories.length === 0) {
 *         // Jojo rabbit had "categories: []" which breaks Neo4J
 *         film.categories = null;
 *     }
 * }
 * ```
 */
data class QuickbookFilmInternal(
	/**
	 * @sample 6322
	 */
	val id: Long,

	/**
	 * @sample 43599
	 */
	override val edi: Long,

	/**
	 * @sample "2D - Despicable Me 2"
	 */
	override val title: String,

	/**
	 * @sample "U"
	 */
	val cert: String,

	/**
	 * @sample "/assets/media/films/6235_poster.jpg"
	 */
	val poster: URI?,

	/**
	 * @sample "http://webcache1.bbccustomerpublishing.com/cineworld/trailers/Despicable Me 2 _qtp.mp4"
	 */
	val trailer: URI?,

	/**
	 * @sample 65
	 */
	val weighted: Int,

	/**
	 * @sample "20130628"
	 */
	val release: String,

	/**
	 * @sample 98
	 */
	val length: Int,

	/**
	 * @sample "Steve Carell, Al Pacino, Kristen Wiig, Ken Jeong, Steve Coogan, Russell Brand, Miranda Cosgrove"
	 */
	val actors: String,
) : QuickbookFilm
