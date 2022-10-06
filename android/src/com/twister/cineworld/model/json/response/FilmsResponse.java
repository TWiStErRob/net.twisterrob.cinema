package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldFilm;

/**
 * <p>
 * Use this to retrieve an array of Cineworld films ordered by title and returned in JSON format. Some films on the
 * Cineworld site are 'stubbed'- this means that film data has not yet been programmed, so only the edi and title will
 * be returned, even if the full parameter is set to true. An example of what will be returned is below (use the full
 * parameter to retrieve more data for each film):
 * 
 * <pre>
 * {"films":[{
 *     "edi":34081,
 *     "title":"Sherlock Holmes"
 * }]}
 * </pre>
 * 
 * </p>
 * <p>
 * If any errors should occur they will be returned as an array:
 * 
 * <pre>
 * {"errors":["valid key not supplied"]}
 * </pre>
 * 
 * </p>
 * 
 * @author papp.robert.s
 * @see CineworldFilm
 */
public class FilmsResponse extends BaseListResponse<CineworldFilm> {
	@SerializedName("films")
	private List<CineworldFilm>	m_films;

	public List<CineworldFilm> getFilms() {
		return m_films;
	}

	public void setFilms(final List<CineworldFilm> films) {
		m_films = films;
	}

	@Override
	public List<CineworldFilm> getList() {
		return Collections.unmodifiableList(getFilms());
	}
}
