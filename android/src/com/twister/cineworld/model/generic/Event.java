package com.twister.cineworld.model.generic;

public class Event extends GenericBase {
	private static final long	serialVersionUID	= -553833652893955957L;

	private String				m_code;
	private String				m_name;

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
