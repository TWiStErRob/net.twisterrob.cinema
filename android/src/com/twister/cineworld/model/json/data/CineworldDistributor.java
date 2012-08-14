package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * Example JSON:
 * 
 * <pre>
 * {
 * id: 523,
 * name: "Miramax"
 * }
 * </pre>
 */
public class CineworldDistributor extends CineworldBase {
	@SerializedName("id")
	private int		m_id;
	@SerializedName("name")
	private String	m_name;

	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}
}
