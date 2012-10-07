package com.twister.cineworld.model.generic;

public class Performance extends GenericBase {
	private static final long	serialVersionUID	= -7455562894972197071L;

	private String				m_time;
	private boolean				m_available;
	private String				m_type;
	private boolean				m_audioDescribed;
	private boolean				m_subtitled;
	private String				m_bookingUrl;
	@SuppressWarnings("unused")
	private String				m_ss;											// TODO what is this?

	public String getTime() {
		return m_time;
	}

	public void setTime(final String time) {
		m_time = time;
	}

	public boolean isAvailable() {
		return m_available;
	}

	public void setAvailable(final boolean available) {
		m_available = available;
	}

	public String getType() {
		return m_type;
	}

	public void setType(final String type) {
		m_type = type;
	}

	public boolean isAudioDescribed() {
		return m_audioDescribed;
	}

	public void setAudioDescribed(final boolean audioDescribed) {
		m_audioDescribed = audioDescribed;
	}

	public boolean isSubtitled() {
		return m_subtitled;
	}

	public void setSubtitled(final boolean subtitled) {
		m_subtitled = subtitled;
	}

	public String getBookingUrl() {
		return m_bookingUrl;
	}

	public void setBookingUrl(final String bookingUrl) {
		m_bookingUrl = bookingUrl;
	}
}
