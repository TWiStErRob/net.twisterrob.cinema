package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * <p>
 * This query returns a list of unique distributors for the films programmed at Cineworld.
 * </p>
 * <p>
 * Example JSON:
 * 
 * <pre>
 * {
 * id: 523,
 * name: "Miramax"
 * }
 * </pre>
 * 
 * </p>
 */
public class CineworldDistributor extends CineworldBase {
	@SerializedName("id")
	private int		m_id;
	@SerializedName("name")
	private String	m_name;

	/**
	 * @return System id for the distributor. This is the value that should be used by when making calls to the API that
	 *         accept a distributor value
	 */
	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	/**
	 * @return Full distributor name
	 */
	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}
}
