package com.twister.cineworld.model.json.data;

import com.twister.cineworld.model.json.request.DatesRequest;

/**
 * <p>
 * This query returns a list of dates that have programmed performances. The results can
 * be filtered by supplying optional film, date and cinema parameters. These can all take
 * multiple values, so for example it is possible to search for all dates on which a
 * cinema is showing a particular film.
 * </p>
 * <p>
 * Example JSON:
 * 
 * <pre>
 * &quot;20120811&quot;
 * </pre>
 * 
 * </p>
 * 
 * @see DatesRequest
 */
public class CineworldDate extends CineworldBase {
	private String m_date; // TOD int or calendar or anything

	/**
	 * @return Date (format yyyymmdd) there is a performance scheduled
	 */
	public String getDate() {
		return m_date;
	}

	public void setDate(final String date) {
		m_date = date;
	}
}
