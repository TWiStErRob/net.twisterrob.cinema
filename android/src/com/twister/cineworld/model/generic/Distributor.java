package com.twister.cineworld.model.generic;

public class Distributor extends GenericBase {
	private int		m_id;
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
