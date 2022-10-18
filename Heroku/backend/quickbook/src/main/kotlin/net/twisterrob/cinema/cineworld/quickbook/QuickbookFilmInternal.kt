@file:Suppress("unused")

package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonProperty
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
	@JsonProperty("id")
	val id: Long,

	/**
	 * @sample 43599
	 */
	@JsonProperty("edi")
	override val edi: Long,

	/**
	 * @sample "2D - Despicable Me 2"
	 */
	@JsonProperty("title")
	override val title: String,

	/**
	 * @sample "U"
	 */
	@JsonProperty("cert")
	val cert: String,

	/**
	 * @sample "/assets/media/films/6235_poster.jpg"
	 */
	@JsonProperty("poster")
	val poster: URI?,

	/**
	 * @sample "http://webcache1.bbccustomerpublishing.com/cineworld/trailers/Despicable Me 2 _qtp.mp4"
	 */
	@JsonProperty("trailer")
	val trailer: URI?,

	/**
	 * @sample 65
	 */
	@JsonProperty("weighted")
	val weighted: Int,

	/**
	 * @sample "20130628"
	 */
	@JsonProperty("release")
	val release: String,

	/**
	 * @sample 98
	 */
	@JsonProperty("length")
	val length: Int,

	/**
	 * @sample "Steve Carell, Al Pacino, Kristen Wiig, Ken Jeong, Steve Coogan, Russell Brand, Miranda Cosgrove"
	 */
	@JsonProperty("actors")
	val actors: String,
) : QuickbookFilm
