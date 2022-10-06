package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * <p>
 * This query returns a list of the current events being run by Cineworld. For example the Edinburgh Film Festival will
 * appear as an event, and all films being shown at the event could be retrieved by using the event parameter.
 * </p>
 * <p>
 * Example JSON:
 * 
 * <pre>
 * {
 * code: "Olympics",
 * name: "London 2012 Olympics"
 * }
 * </pre>
 * 
 * </p>
 */
public class CineworldEvent extends CineworldBase {
	@SerializedName("code")
	private String	m_code;
	@SerializedName("name")
	private String	m_name;

	/**
	 * @return System code for the event. This is the value that should be used by when making calls to the API that
	 *         accept an event value
	 */
	public String getCode() {
		return m_code;
	}

	public void setCode(final String code) {
		m_code = code;
	}

	/**
	 * @return Full name for the event
	 */
	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}
}
