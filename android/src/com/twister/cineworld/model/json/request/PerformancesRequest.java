package com.twister.cineworld.model.json.request;

import java.net.URL;
import java.util.Calendar;

import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.response.PerformancesResponse;

/**
 * <p>
 * This query returns a list of performances that are programmed for a particular cinema, film and date.
 * </p>
 * <p>
 * Example request:<br>
 * <code>http://www.cineworld.com/api/quickbook/performances?key=key&cinema=23&film=54321&date=20100801</code>
 * </p>
 * 
 * @author papp.robert.s
 */
public class PerformancesRequest extends BaseListRequest<CineworldPerformance> {
	/**
	 * Relative URI to the base API URL.
	 */
	private static final String	REQUEST_URI		= "quickbook/performances";
	/**
	 * String representation of request type.
	 */
	private static final String	REQUEST_TYPE	= "performances";
	private Integer				m_cinema;
	private Integer				m_date;
	private Integer				m_film;

	/**
	 * Creates an empty request, use the setters to provide filtering parameters.
	 */
	public PerformancesRequest() {
	}

	/**
	 * @param cinema (Required) Cinema ID to retrieve performances for.
	 * @param date (Required) Date (format yyyymmdd) to retrieve performances for.
	 * @param film (Required) Film EDI to retrieve performances for.
	 */
	public PerformancesRequest(final Integer cinema, final Integer film, final Integer date) {
		super();
		m_cinema = cinema;
		m_film = film;
		m_date = date;
	}

	/**
	 * @param key (Required) Your developer API key.
	 * @param territory (Optional, default GB) Sets which territory to return cinemas for, valid values for United
	 *            Kingdom and Ireland are; GB and IE.
	 * @param callback (Optional, no default)Wraps the response JSON in the callback function specified to allow cross
	 *            browser scripting, note that if you use jQuery and JSONP the callback parameter is automatically added
	 *            for you.
	 * @param cinema (Required) Cinema ID to retrieve performances for.
	 * @param date (Required) Date (format yyyymmdd) to retrieve performances for.
	 * @param film (Required) Film EDI to retrieve performances for.
	 */
	public PerformancesRequest(final String key, final String territory, final String callback, final Integer cinema,
			final Integer film, final Integer date) {
		super(key, territory, callback);
		m_cinema = cinema;
		m_film = film;
		m_date = date;
	}

	/**
	 * <p>
	 * Example request:<br>
	 * <code>http://www.cineworld.com/api/quickbook/performances?key=key&cinema=23&film=54321&date=20100801</code>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public URL getURL() {
		return BaseListRequest.makeUrl(PerformancesRequest.REQUEST_URI,
				BaseListRequest.KEY_CINEMA, m_cinema,
				BaseListRequest.KEY_FILM, m_film,
				BaseListRequest.KEY_DATE, m_date);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return PerformancesRequest.REQUEST_TYPE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see PerformancesResponse
	 */
	@Override
	public Class<PerformancesResponse> getResponseClass() {
		return PerformancesResponse.class;
	}

	/**
	 * @required
	 * @return Cinema ID to retrieve performances for.
	 * @see CineworldCinema
	 */
	public Integer getCinema() {
		return m_cinema;
	}

	/**
	 * @required
	 * @param cinema Cinema ID to retrieve performances for.
	 * @see CineworldCinema
	 */
	public void setCinema(final Integer cinema) {
		m_cinema = cinema;
	}

	/**
	 * @required
	 * @param cinema Cinema ID to retrieve performances for.
	 * @see CineworldCinema
	 */
	public void setCinema(final CineworldCinema cinema) {
		setCinema(cinema == null? null : cinema.getId());
	}

	/**
	 * @required
	 * @return Film EDI to retrieve performances for.
	 * @see CineworldFilm
	 */
	public Integer getFilm() {
		return m_film;
	}

	/**
	 * @required
	 * @param film Film EDI to retrieve performances for.
	 * @see CineworldFilm
	 */
	public void setFilm(final Integer film) {
		m_film = film;
	}

	/**
	 * @required
	 * @param film Film EDI to retrieve performances for.
	 * @see CineworldFilm
	 */
	public void setFilm(final CineworldFilm film) {
		setFilm(film == null? null : film.getId());
	}

	/**
	 * @required
	 * @return Date (format yyyymmdd) to retrieve performances for.
	 * @see CineworldDate
	 */
	public Integer getDate() {
		return m_date;
	}

	/**
	 * @required
	 * @param date Date (format yyyymmdd) to retrieve performances for.
	 * @see CineworldDate
	 */
	public void setDate(final Integer date) {
		m_date = date;
	}

	/**
	 * @required
	 * @param date Date (format yyyymmdd) to retrieve performances for.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final CineworldDate date) {
		setDate(date.getDate());
	}

	/**
	 * @required
	 * @param date Date (format yyyymmdd) to retrieve performances for.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final String date) {
		setDate(date == null? null : Integer.parseInt(date));
	}

	/**
	 * @required
	 * @param date Date (format yyyymmdd) to retrieve performances for.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final Calendar date) {
		setDate(date == null? null : String.format("%1$tY%1$tm%1$td", date));
	}
}
