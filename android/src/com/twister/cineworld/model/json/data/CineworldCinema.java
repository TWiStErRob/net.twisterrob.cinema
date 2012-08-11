package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
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
 */
public class CineworldCinema extends CineworldBase {
	@SerializedName("id")
	private long	m_id;
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

	public long getId() {
		return m_id;
	}

	public void setId(final long id) {
		m_id = id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}

	public String getCinemaUrl() {
		return m_cinemaUrl;
	}

	public void setCinemaUrl(final String cinemaUrl) {
		m_cinemaUrl = cinemaUrl;
	}

	public String getAddress() {
		return m_address;
	}

	public void setAddress(final String address) {
		m_address = address;
	}

	public String getPostcode() {
		return m_postcode;
	}

	public void setPostcode(final String postcode) {
		m_postcode = postcode;
	}

	public String getTelephone() {
		return m_telephone;
	}

	public void setTelephone(final String telephone) {
		m_telephone = telephone;
	}

}
