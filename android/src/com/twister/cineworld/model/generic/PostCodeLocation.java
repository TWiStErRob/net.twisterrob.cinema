package com.twister.cineworld.model.generic;

public class PostCodeLocation extends GenericBase {
	private static final long	serialVersionUID	= 3949366660122437329L;

	private String				m_postCode;
	private Location			m_location;

	public PostCodeLocation(final String postCode, final Location location) {
		m_postCode = postCode;
		m_location = location;
	}

	public Location getLocation() {
		return m_location;
	}

	public String getPostCode() {
		return m_postCode;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", m_postCode, m_location);
	}
}
