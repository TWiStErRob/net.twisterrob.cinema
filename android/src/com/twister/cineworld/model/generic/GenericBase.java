package com.twister.cineworld.model.generic;

import java.io.Serializable;
import java.util.Calendar;

public class GenericBase implements Serializable {
	private static final long	serialVersionUID	= -3314136060033364263L;

	private String				m_source;
	private Calendar			m_lastUpdate;

	/**
	 * Where is this Object originated from (i.e. who created it)?
	 * 
	 * @return the source of this object
	 */
	public String getSource() {
		return m_source;
	}

	public void setSource(final String source) {
		this.m_source = source;
	}

	/**
	 * The date/time of the last DB update
	 */
	public Calendar getLastUpdate() {
		return m_lastUpdate;
	}

	public void setLastUpdate(final Calendar lastUpdate) {
		m_lastUpdate = lastUpdate;
	}
}
