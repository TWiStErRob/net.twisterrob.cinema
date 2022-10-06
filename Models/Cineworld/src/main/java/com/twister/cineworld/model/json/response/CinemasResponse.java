package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldCinema;

/**
 * <p>
 * Use this to retrieve an array of Cineworld cinemas ordered by name and returned in JSON format. An example of what
 * will be returned is below (use the full parameter to retrieve more data for each cinema):
 * 
 * <pre>
 * {"cinemas":[{
 *     "id":78,
 *     "name":"Aberdeen-Union-Square",
 *     "cinema_url":"http://www.cineworld.co.uk/cinemas/78"
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
 * @see CineworldCinema
 */
public class CinemasResponse extends BaseListResponse<CineworldCinema> {
	@SerializedName("cinemas")
	private List<CineworldCinema>	m_cinemas;

	public List<CineworldCinema> getCinemas() {
		return m_cinemas;
	}

	public void setCinemas(final List<CineworldCinema> cinemas) {
		m_cinemas = cinemas;
	}

	@Override
	public List<CineworldCinema> getList() {
		return Collections.unmodifiableList(getCinemas());
	}
}
