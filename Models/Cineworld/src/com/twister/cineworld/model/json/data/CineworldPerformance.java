package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.request.PerformancesRequest;

/**
 * <p>
 * This query returns a list of performances that are programmed for a particular cinema,
 * film and date.
 * </p>
 * <p>
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
 * 
 * </p>
 * 
 * @see PerformancesRequest
 */
public class CineworldPerformance extends CineworldBase {
	@SerializedName("time") private String m_time;
	@SerializedName("available") private boolean m_available;
	@SerializedName("type") private String m_type;
	@SerializedName("ad") private boolean m_audioDescribed;
	@SerializedName("subtitled") private boolean m_subtitled;
	@SerializedName("booking_url") private String m_bookingUrl;
	@SerializedName("ss") private String m_ss; // TOD what is this?

	/**
	 * @return Time (format hh:mm) at which the performance is scheduled to start
	 */
	public String getTime() {
		return m_time;
	}

	public void setTime(final String time) {
		m_time = time;
	}

	/**
	 * @return Whether it is possible to book tickets for this performance
	 */
	public boolean isAvailable() {
		return m_available;
	}

	public void setAvailable(final boolean available) {
		m_available = available;
	}

	/**
	 * Possible performance type codes are:
	 * <ul>
	 * <li>reg - Regular
	 * <li>vip - VIP
	 * <li>del - Delux
	 * <li>digital - Digital
	 * <li>m4j - Movies for Juniors
	 * <li>dbox - D-Box
	 * </ul>
	 * 
	 * @return Performance type code
	 */
	public String getType() {
		return m_type;
	}

	public void setType(final String type) {
		m_type = type;
	}

	/**
	 * @return Whether this performance is audio-described
	 */
	public boolean isAudioDescribed() {
		return m_audioDescribed;
	}

	public void setAudioDescribed(final boolean audioDescribed) {
		m_audioDescribed = audioDescribed;
	}

	/**
	 * @return Whether this performance is subtitled
	 */
	public boolean isSubtitled() {
		return m_subtitled;
	}

	public void setSubtitled(final boolean subtitled) {
		m_subtitled = subtitled;
	}

	/**
	 * <p>
	 * The booking url returned in the JSON response can be used to access the booking
	 * page with prepopulated film, cinema, date and time information. We use the key
	 * parameter to track the number of hits that you direct to the booking page.
	 * </p>
	 * <p>
	 * If you would like us to be able to track specific campaigns that you will run, you
	 * need to append another parameter called campaign to the booking url as shown below.
	 * 
	 * <pre>
	 * http://www.cineworld.co.uk/booking?performance=18697&key=aabbcc&campaign=promotionalcampaign1
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * This campaign identifier can be any string you like and you don't need to register
	 * this with us.
	 * </p>
	 * 
	 * @return Absolute URL to the performance booking page
	 */
	public String getBookingUrl() {
		return m_bookingUrl;
	}

	public void setBookingUrl(final String bookingUrl) {
		m_bookingUrl = bookingUrl;
	}
}
