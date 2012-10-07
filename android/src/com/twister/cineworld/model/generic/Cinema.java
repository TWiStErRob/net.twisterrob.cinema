package com.twister.cineworld.model.generic;

import com.google.android.maps.GeoPoint;

public class Cinema extends GenericBase {
	private static final long	serialVersionUID	= 6544996686705019485L;
	private int					m_companyId;
	private int					m_id;
	private String				m_name;
	private String				m_detailsUrl;
	private String				m_territory;
	private String				m_address;
	private String				m_postcode;
	private String				m_telephone;
	private GeoPoint			m_location;

	public int getCompanyId() {
		return m_companyId;
	}

	public void setCompanyId(final int companyId) {
		m_companyId = companyId;
	}

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

	public String getDetailsUrl() {
		return m_detailsUrl;
	}

	public void setDetailsUrl(final String detailsUrl) {
		m_detailsUrl = detailsUrl;
	}

	public String getTerritory() {
		return m_territory;
	}

	public void setTerritory(final String territory) {
		m_territory = territory;
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
		return m_location;
	}

	public void setLocation(final GeoPoint location) {
		m_location = location;
	}
}
