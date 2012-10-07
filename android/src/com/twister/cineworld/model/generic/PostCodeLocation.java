package com.twister.cineworld.model.generic;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

public class PostCodeLocation implements Serializable {
	private static final long	serialVersionUID	= 3949366660122437329L;

	private String				m_postCode;
	private GeoPoint			m_location;

	public PostCodeLocation(final String postCode, final GeoPoint location) {
		m_postCode = postCode;
		m_location = location;
	}

	public GeoPoint getLocation() {
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
