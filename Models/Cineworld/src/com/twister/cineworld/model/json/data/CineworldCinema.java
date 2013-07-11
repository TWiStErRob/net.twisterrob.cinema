package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.request.CinemasRequest;

/**
 * <p>
 * This query returns a list of cinemas that have programmed performances. The results can be filtered by supplying
 * optional film, date and cinema parameters. These can all take multiple values, so for example it is possible to
 * search for all cinemas showing film1 or film2 on a specific date.
 * </p>
 * <p>
 * Example JSON:
 * 
 * <pre>
 * {
 * id: 30, 
 * name: "Hammersmith", 
 * cinema_url: "http://www.cineworld.co.uk/cinemas/30", 
 * address: "207 King Street, Hammersmith, London", 
 * postcode: "W6 9JT", 
 * telephone: "0871 200 2000"
 * }
 * </pre>
 * 
 * </p>
 * 
 * @see CinemasRequest
 */
public class CineworldCinema extends CineworldBase {
	@SerializedName("id")
	private int		m_id;
	@SerializedName("name")
	private String	m_name;
	@SerializedName("cinema_url")
	private String	m_cinemaUrl;
	@SerializedName("address")
	private String	m_address;
	@SerializedName("postcode")
	private String	m_postcode;
	@SerializedName("telephone")
	private String	m_telephone;

	/**
	 * @return Cinema ID
	 */
	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	/**
	 * @return Cinema name
	 */
	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}

	/**
	 * @return Absolute URL the cinema page
	 */
	public String getCinemaUrl() {
		return m_cinemaUrl;
	}

	public void setCinemaUrl(final String cinemaUrl) {
		m_cinemaUrl = cinemaUrl;
	}

	/**
	 * @return Full cinema address (all lines, excluding postcode)
	 */
	public String getAddress() {
		return m_address;
	}

	public void setAddress(final String address) {
		m_address = address;
	}

	/**
	 * @return Cinema postcode
	 */
	public String getPostcode() {
		return m_postcode;
	}

	public void setPostcode(final String postcode) {
		m_postcode = postcode;
	}

	/**
	 * @return Cinema telephone number
	 */
	public String getTelephone() {
		return m_telephone;
	}

	public void setTelephone(final String telephone) {
		m_telephone = telephone;
	}
}
