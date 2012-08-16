package com.twister.cineworld.model.json.request;

import java.net.URL;
import java.util.Calendar;

import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.response.CinemasResponse;

/**
 * <p>
 * This query returns a list of cinemas that have programmed performances. The results can be filtered by supplying
 * optional film, date and cinema parameters. These can all take multiple values, so for example it is possible to
 * search for all cinemas showing film1 or film2 on a specific date.
 * </p>
 * <p>
 * Example request:<br>
 * <code>http://www.cineworld.com/api/quickbook/cinemas?key=key&film=12345&film=54321&date=20100801</code>
 * </p>
 * 
 * @author papp.robert.s
 */
public class CinemasRequest extends BaseListRequest<CineworldCinema> {
	private static final String	REQUEST_TYPE	= "quickbook/cinemas";
	private Boolean	            m_full;
	private Integer	            m_film;
	private Integer	            m_date;
	private Integer	            m_cinema;
	private String	            m_category;
	private String	            m_event;
	private Integer	            m_distributor;

	/**
	 * Creates an empty request, use the setters to provide filtering parameters.
	 */
	public CinemasRequest() {
	}

	/**
	 * @param full (Optional, default false) Returns additional fields for each cinema, namely; address, postcode and
	 *            telephone.
	 * @param film (Optional, no default) Film EDI to retrieve all cinemas showing this film, or for all films if
	 *            excluded. More than one edi can be passed in, by assigning multiple values: film=1234&film=2345 - this
	 *            will mean cinemas are retrieved that are showing at least ONE of the films.
	 * @param date (Optional, no default) Date (format yyyymmdd) - retrieves cinemas with performances on this date, or
	 *            on any date if excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @param cinema (Optional, no default) Cinema ID to retrieve - this can be passed in if information for a
	 *            particular cinema is required.
	 * @param category (Optional, no default) Category code- this can be passed in to filter the results to only contain
	 *            cinemas that have at least on performance for a film in this category.
	 * @param event (Optional, no default) Event code- this can be passed in to filter the results to only contain
	 *            cinemas that have at least on performance for a film linked to this event.
	 * @param distributor (Optional, no default) Distributor id- this can be passed in to filter the results to only
	 *            contain cinemas that have at least on performance for a film from this distributor.
	 */
	public CinemasRequest(final Boolean full, final Integer film, final Integer date,
	        final Integer cinema, final String category, final String event, final Integer distributor) {
		super();
		m_full = full;
		m_film = film;
		m_date = date;
		m_cinema = cinema;
		m_category = category;
		m_event = event;
		m_distributor = distributor;
	}

	/**
	 * @param key (Required) Your developer API key.
	 * @param territory (Optional, default GB) Sets which territory to return cinemas for, valid values for United
	 *            Kingdom and Ireland are; GB and IE.
	 * @param callback (Optional, no default)Wraps the response JSON in the callback function specified to allow cross
	 *            browser scripting, note that if you use jQuery and JSONP the callback parameter is automatically added
	 *            for you.
	 * @param full (Optional, default false) Returns additional fields for each cinema, namely; address, postcode and
	 *            telephone.
	 * @param film (Optional, no default) Film EDI to retrieve all cinemas showing this film, or for all films if
	 *            excluded. More than one edi can be passed in, by assigning multiple values: film=1234&film=2345 - this
	 *            will mean cinemas are retrieved that are showing at least ONE of the films.
	 * @param date (Optional, no default) Date (format yyyymmdd) - retrieves cinemas with performances on this date, or
	 *            on any date if excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @param cinema (Optional, no default) Cinema ID to retrieve - this can be passed in if information for a
	 *            particular cinema is required.
	 * @param category (Optional, no default) Category code- this can be passed in to filter the results to only contain
	 *            cinemas that have at least on performance for a film in this category.
	 * @param event (Optional, no default) Event code- this can be passed in to filter the results to only contain
	 *            cinemas that have at least on performance for a film linked to this event.
	 * @param distributor (Optional, no default) Distributor id- this can be passed in to filter the results to only
	 *            contain cinemas that have at least on performance for a film from this distributor.
	 */
	public CinemasRequest(final String key, final String territory, final String callback, final Boolean full,
	        final Integer film, final Integer date,
	        final Integer cinema, final String category, final String event, final Integer distributor) {
		super(key, territory, callback);
		m_full = full;
		m_film = film;
		m_date = date;
		m_cinema = cinema;
		m_category = category;
		m_event = event;
		m_distributor = distributor;
	}

	/**
	 * <p>
	 * Example request:<br>
	 * <code>http://www.cineworld.com/api/quickbook/cinemas?key=key&film=12345&film=54321&date=20100801</code>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public URL getURL() {
		return BaseListRequest.makeUrl(CinemasRequest.REQUEST_TYPE,
		        BaseListRequest.KEY_FULL, m_full,
		        BaseListRequest.KEY_FILM, m_film,
		        BaseListRequest.KEY_DATE, m_date,
		        BaseListRequest.KEY_CINEMA, m_cinema,
		        BaseListRequest.KEY_CATEGORY, m_category,
		        BaseListRequest.KEY_EVENT, m_event,
		        BaseListRequest.KEY_DISTRIBUTOR, m_distributor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return "cinemas";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see CinemasResponse
	 */
	@Override
	public Class<CinemasResponse> getResponseClass() {
		return CinemasResponse.class;
	}

	/**
	 * @optional false
	 * @return Returns additional fields for each cinema, namely; address, postcode and telephone.
	 */
	public Boolean getFull() {
		return m_full;
	}

	/**
	 * @optional false
	 * @param full Returns additional fields for each cinema, namely; address, postcode and telephone.
	 */
	public void setFull(final Boolean full) {
		m_full = full;
	}

	/**
	 * @optional N/A
	 * @return Film EDI to retrieve all cinemas showing this film, or for all films if excluded. More than one edi can
	 *         be passed in, by assigning multiple values: film=1234&film=2345 - this will mean cinemas are retrieved
	 *         that are showing at least ONE of the films.
	 * @see CineworldFilm
	 */
	public Integer getFilm() {
		return m_film;
	}

	/**
	 * @optional N/A
	 * @param film Film EDI to retrieve all cinemas showing this film, or for all films if excluded. More than one edi
	 *            can be passed in, by assigning multiple values: film=1234&film=2345 - this will mean cinemas are
	 *            retrieved that are showing at least ONE of the films.
	 * @see CineworldFilm
	 */
	public void setFilm(final Integer film) {
		m_film = film;
	}

	/**
	 * @optional N/A
	 * @param film Film EDI to retrieve all cinemas showing this film, or for all films if excluded. More than one edi
	 *            can be passed in, by assigning multiple values: film=1234&film=2345 - this will mean cinemas are
	 *            retrieved that are showing at least ONE of the films.
	 * @see CineworldFilm
	 */
	public void setFilm(final CineworldFilm film) {
		setFilm(film == null? null : film.getId());
	}

	/**
	 * @optional N/A
	 * @return Date (format yyyymmdd) - retrieves cinemas with performances on this date, or on any date if excluded;
	 *         several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 */
	public Integer getDate() {
		return m_date;
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves cinemas with performances on this date, or on any date if
	 *            excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 */
	public void setDate(final Integer date) {
		m_date = date;
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves cinemas with performances on this date, or on any date if
	 *            excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final CineworldDate date) {
		setDate(date.getDate());
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves cinemas with performances on this date, or on any date if
	 *            excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final String date) {
		setDate(date == null? null : Integer.parseInt(date));
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves cinemas with performances on this date, or on any date if
	 *            excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final Calendar date) {
		setDate(date == null? null : String.format("%1$tY%1$tm%1$td", date));
	}

	/**
	 * @optional N/A
	 * @return Cinema ID to retrieve - this can be passed in if information for a particular cinema is required.
	 * @see CineworldCinema
	 */
	public Integer getCinema() {
		return m_cinema;
	}

	/**
	 * @optional N/A
	 * @param cinema Cinema ID to retrieve - this can be passed in if information for a particular cinema is required.
	 * @see CineworldCinema
	 */
	public void setCinema(final Integer cinema) {
		m_cinema = cinema;
	}

	/**
	 * @optional N/A
	 * @param cinema Cinema ID to retrieve - this can be passed in if information for a particular cinema is required.
	 * @see CineworldCinema
	 */
	public void setCinema(final CineworldCinema cinema) {
		setCinema(cinema == null? null : cinema.getId());
	}

	/**
	 * @optional N/A
	 * @return Category code- this can be passed in to filter the results to only contain cinemas that have at least on
	 *         performance for a film in this category.
	 * @see CineworldCategory
	 */
	public String getCategory() {
		return m_category;
	}

	/**
	 * @optional N/A
	 * @param category Category code- this can be passed in to filter the results to only contain cinemas that have at
	 *            least on performance for a film in this category.
	 * @see CineworldCategory
	 */
	public void setCategory(final String category) {
		m_category = category;
	}

	/**
	 * @optional N/A
	 * @param category Category code- this can be passed in to filter the results to only contain cinemas that have at
	 *            least on performance for a film in this category.
	 * @see CineworldCategory
	 */
	public void setCategory(final CineworldCategory category) {
		setCategory(category == null? null : category.getCode());
	}

	/**
	 * @optional N/A
	 * @return Event code- this can be passed in to filter the results to only contain cinemas that have at least on
	 *         performance for a film linked to this event.
	 * @see CineworldEvent
	 */
	public String getEvent() {
		return m_event;
	}

	/**
	 * @optional N/A
	 * @param event Event code- this can be passed in to filter the results to only contain cinemas that have at least
	 *            on performance for a film linked to this event.
	 * @see CineworldEvent
	 */
	public void setEvent(final String event) {
		m_event = event;
	}

	/**
	 * @optional N/A
	 * @param event Event code- this can be passed in to filter the results to only contain cinemas that have at least
	 *            on performance for a film linked to this event.
	 * @see CineworldEvent
	 */
	public void setEvent(final CineworldEvent event) {
		setEvent(event == null? null : event.getCode());
	}

	/**
	 * @optional N/A
	 * @return Distributor id- this can be passed in to filter the results to only contain cinemas that have at least on
	 *         performance for a film from this distributor.
	 * @see CineworldDistributor
	 */
	public Integer getDistributor() {
		return m_distributor;
	}

	/**
	 * @optional N/A
	 * @param distributor Distributor id- this can be passed in to filter the results to only contain cinemas that have
	 *            at least on performance for a film from this distributor.
	 * @see CineworldDistributor
	 */
	public void setDistributor(final Integer distributor) {
		m_distributor = distributor;
	}

	/**
	 * @optional N/A
	 * @param distributor Distributor id- this can be passed in to filter the results to only contain cinemas that have
	 *            at least on performance for a film from this distributor.
	 * @see CineworldDistributor
	 */
	public void setDistributor(final CineworldDistributor distributor) {
		setDistributor(distributor == null? null : distributor.getId());
	}
}
