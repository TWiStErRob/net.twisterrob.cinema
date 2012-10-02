package com.twister.cineworld.model.generic;

import java.io.IOException;
import java.util.List;

import android.location.*;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.App;

public class Cinema extends GenericBase {
	private int			m_id;
	private String		m_name;
	private String		m_url;
	private String		m_address;
	private String		m_postcode;
	private String		m_telephone;
	private GeoPoint	m_location;

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

	public String getUrl() {
		return m_url;
	}

	public void setUrl(final String url) {
		m_url = url;
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

	public GeoPoint getLocation() {
		if (m_location == null) {
			GeoPoint loc = GeoCache.getGeoPoint(m_postcode);
			if (loc == null && m_postcode != null) {
				Geocoder coder = new Geocoder(App.getInstance());
				try {
					List<Address> locs = coder.getFromLocationName(m_postcode, 1);
					if (!locs.isEmpty()) {
						Address address = locs.get(0);
						loc = new GeoPoint((int) (address.getLatitude() * 1e6),
								(int) (locs.get(0).getLongitude() * 1e6));
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			m_location = loc;
		}
		return m_location;
	}

	public void setLocation(final GeoPoint location) {
		m_location = location;
	}
}
