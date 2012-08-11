package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * Example JSON:
 * 
 * <pre>
 * {
 * code: "Olympics",
 * name: "London 2012 Olympics"
 * }
 * </pre>
 */
public class CineworldEvent extends CineworldBase {
	@SerializedName("code")
	private String	m_code;
	@SerializedName("name")
	private String	m_name;

	public String getCode() {
		return m_code;
	}

	public void setCode(final String code) {
		m_code = code;
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}
}
