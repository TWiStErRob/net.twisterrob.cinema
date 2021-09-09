package com.twister.cineworld.model.generic;

public class Category extends GenericBase {
	private static final long	serialVersionUID	= -4194934427901520341L;

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
