package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * Example JSON:
 * 
 * <pre>
 * {
 * time: "11:00",
 * available: true,
 * type: "vip",
 * ad: false,
 * subtitled: false,
 * ss: false,
 * booking_url: "http://www.cineworld.co.uk/booking?performance=4895012&key=9qfgpF7B"
 * }
 * </pre>
 */
public class CineworldPerformance extends CineworldBase {
	@SerializedName("time")
	private String	m_time;
	@SerializedName("available")
	private boolean	m_available;
	@SerializedName("type")
	private String	m_type;
	@SerializedName("ad")
	private boolean	m_audioDescribed;
	@SerializedName("subtitled")
	private boolean	m_subtitled;
	@SerializedName("ss")
	private String	m_ss;	          // TODO what is this?
	@SerializedName("booking_url")
	private String	m_bookingUrl;

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
