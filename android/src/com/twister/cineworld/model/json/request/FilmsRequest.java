package com.twister.cineworld.model.json.request;

import java.net.URL;
import java.util.Calendar;

import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.response.FilmsResponse;

/**
 * <p>
 * This query returns a list of films that have programmed performances. The results can be filtered by supplying
 * optional film, date and cinema parameters. These can all take multiple values, so for example it is possible to
 * search for all films showing at cinema1 on two specific dates.
 * </p>
 * <p>
 * Example request:<br>
 * <code>http://www.cineworld.com/api/quickbook/films?key=key&cinema=23&date=20100801&date=20100802</code>
 * </p>
 * <p>
 * Calls to this part of the API will return films as well as other content Cineworld show in their screens such as
 * Operas. The EDI number used in responses are supplied by Nielsen-EDI for films, other content will have unique
 * numbers not supplied by Nielsen-EDI.
 * </p>
 * 
 * @author papp.robert.s
 */
public class FilmsRequest extends BaseListRequest<CineworldFilm> {
	private static final String	REQUEST_TYPE	= "quickbook/films";
	private Boolean	            m_full;
	private Integer	            m_cinema;
	private Integer	            m_date;
	private Integer	            m_film;
	private String	            m_category;
	private String	            m_event;
	private Integer	            m_distributor;

	/**
	 * Creates an empty request, use the setters to provide filtering parameters.
	 */
	public FilmsRequest() {
	}

	/**
	 * @param full (Optional, default false) Returns additional fields for each film, namely; classification, advisory,
	 *            poster_url, still_url and film_url.
	 * @param cinema (Optional, no default) Cinema ID to retrieve films for, or for all cinemas if excluded. More than
	 *            one cinema id can be passed in, by assigning multiple values: cinema=1&cinema=2 - this will mean films
	 *            are retrieved for which there are performances at at least ONE of the cinemas.
	 * @param date (Optional, no default) Date (format yyyymmdd) - retrieves films with performances on this date, or on
	 *            any date if excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @param film (Optional, no default) Film EDI to retrieve - this can be passed in if information for a particular
	 *            film is required.
	 * @param category (Optional, no default) Category code- this can be passed in to filter the results to only contain
	 *            films in this category.
	 * @param event (Optional, no default) Event code- this can be passed in to filter the results to only contain films
	 *            linked to this event.
	 * @param distributor (Optional, no default) Distributor id- this can be passed in to filter the results to only
	 *            contain films from this distributor.
	 */
	public FilmsRequest(final Boolean full, final Integer cinema, final Integer date, final Integer film,
	        final String category, final String event, final Integer distributor) {
		super();
		m_full = full;
		m_cinema = cinema;
		m_date = date;
		m_film = film;
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
	 * @param full (Optional, default false) Returns additional fields for each film, namely; classification, advisory,
	 *            poster_url, still_url and film_url.
	 * @param cinema (Optional, no default) Cinema ID to retrieve films for, or for all cinemas if excluded. More than
	 *            one cinema id can be passed in, by assigning multiple values: cinema=1&cinema=2 - this will mean films
	 *            are retrieved for which there are performances at at least ONE of the cinemas.
	 * @param date (Optional, no default) Date (format yyyymmdd) - retrieves films with performances on this date, or on
	 *            any date if excluded; several dates can be passed in: date=20100810&date=20100811.
	 * @param film (Optional, no default) Film EDI to retrieve - this can be passed in if information for a particular
	 *            film is required.
	 * @param category (Optional, no default) Category code- this can be passed in to filter the results to only contain
	 *            films in this category.
	 * @param event (Optional, no default) Event code- this can be passed in to filter the results to only contain films
	 *            linked to this event.
	 * @param distributor (Optional, no default) Distributor id- this can be passed in to filter the results to only
	 *            contain films from this distributor.
	 */
	public FilmsRequest(final String key, final String territory, final String callback, final Boolean full,
	        final Integer cinema, final Integer date, final Integer film, final String category, final String event,
	        final Integer distributor) {
		super(key, territory, callback);
		m_full = full;
		m_cinema = cinema;
		m_date = date;
		m_film = film;
		m_category = category;
		m_event = event;
		m_distributor = distributor;
	}

	/**
	 * <p>
	 * Example request:<br>
	 * <code>http://www.cineworld.com/api/quickbook/films?key=key&cinema=23&date=20100801&date=20100802</code>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public URL getURL() {
		return BaseListRequest.makeUrl(FilmsRequest.REQUEST_TYPE,
		        BaseListRequest.KEY_FULL, m_full,
		        BaseListRequest.KEY_CINEMA, m_cinema,
		        BaseListRequest.KEY_DATE, m_date,
		        BaseListRequest.KEY_FILM, m_film,
		        BaseListRequest.KEY_CATEGORY, m_category,
		        BaseListRequest.KEY_EVENT, m_event,
		        BaseListRequest.KEY_DISTRIBUTOR, m_distributor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return "films";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see FilmsResponse
	 */
	@Override
	public Class<FilmsResponse> getResponseClass() {
		return FilmsResponse.class;
	}

	/**
	 * @optional false
	 * @return Returns additional fields for each film, namely; classification, advisory, poster_url, still_url and
	 *         film_url.
	 */
	public Boolean getFull() {
		return m_full;
	}

	/**
	 * @optional false
	 * @param full Returns additional fields for each film, namely; classification, advisory, poster_url, still_url and
	 *            film_url.
	 */
	public void setFull(final Boolean full) {
		m_full = full;
	}

	/**
	 * @optional N/A
	 * @return Cinema ID to retrieve films for, or for all cinemas if excluded. More than one cinema id can be passed
	 *         in, by assigning multiple values: cinema=1&cinema=2 - this will mean films are retrieved for which there
	 *         are performances at at least ONE of the cinemas.
	 * @see CineworldCinema
	 */
	public Integer getCinema() {
		return m_cinema;
	}

	/**
	 * @optional N/A
	 * @param cinema Cinema ID to retrieve films for, or for all cinemas if excluded. More than one cinema id can be
	 *            passed in, by assigning multiple values: cinema=1&cinema=2 - this will mean films are retrieved for
	 *            which there are performances at at least ONE of the cinemas.
	 * @see CineworldCinema
	 */
	public void setCinema(final Integer cinema) {
		m_cinema = cinema;
	}

	/**
	 * @optional N/A
	 * @param cinema Cinema ID to retrieve films for, or for all cinemas if excluded. More than one cinema id can be
	 *            passed in, by assigning multiple values: cinema=1&cinema=2 - this will mean films are retrieved for
	 *            which there are performances at at least ONE of the cinemas.
	 * @see CineworldCinema
	 */
	public void setCinema(final CineworldCinema cinema) {
		setCinema(cinema == null? null : cinema.getId());
	}

	/**
	 * @optional N/A
	 * @return Date (format yyyymmdd) - retrieves films with performances on this date, or on any date if excluded;
	 *         several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 */
	public Integer getDate() {
		return m_date;
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves films with performances on this date, or on any date if excluded;
	 *            several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 */
	public void setDate(final Integer date) {
		m_date = date;
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves films with performances on this date, or on any date if excluded;
	 *            several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final CineworldDate date) {
		setDate(date.getDate());
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves films with performances on this date, or on any date if excluded;
	 *            several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final String date) {
		setDate(date == null? null : Integer.parseInt(date));
	}

	/**
	 * @optional N/A
	 * @param date Date (format yyyymmdd) - retrieves films with performances on this date, or on any date if excluded;
	 *            several dates can be passed in: date=20100810&date=20100811.
	 * @see CineworldDate
	 * @throws NumberFormatException if date is invalid
	 */
	public void setDate(final Calendar date) {
		setDate(date == null? null : String.format("%1$tY%1$tm%1$td", date));
	}

	/**
	 * @optional N/A
	 * @return Film EDI to retrieve - this can be passed in if information for a particular film is required.
	 * @see CineworldFilm
	 */
	public Integer getFilm() {
		return m_film;
	}

	/**
	 * @optional N/A
	 * @param film Film EDI to retrieve - this can be passed in if information for a particular film is required.
	 * @see CineworldFilm
	 */
	public void setFilm(final Integer film) {
		m_film = film;
	}

	/**
	 * @optional N/A
	 * @param film Film EDI to retrieve - this can be passed in if information for a particular film is required.
	 * @see CineworldFilm
	 */
	public void setFilm(final CineworldFilm film) {
		setFilm(film == null? null : film.getId());
	}

	/**
	 * @optional N/A
	 * @return Category code- this can be passed in to filter the results to only contain films in this category.
	 * @see CineworldCategory
	 */
	public String getCategory() {
		return m_category;
	}

	/**
	 * @optional N/A
	 * @param category Category code- this can be passed in to filter the results to only contain films in this
	 *            category.
	 * @see CineworldCategory
	 */
	public void setCategory(final String category) {
		m_category = category;
	}

	/**
	 * @optional N/A
	 * @param category Category code- this can be passed in to filter the results to only contain films in this
	 *            category.
	 * @see CineworldCategory
	 */
	public void setCategory(final CineworldCategory category) {
		setCategory(category == null? null : category.getCode());
	}

	/**
	 * @optional N/A
	 * @return Event code- this can be passed in to filter the results to only contain films linked to this event.
	 * @see CineworldEvent
	 */
	public String getEvent() {
		return m_event;
	}

	/**
	 * @optional N/A
	 * @param event Event code- this can be passed in to filter the results to only contain films linked to this event.
	 * @see CineworldEvent
	 */
	public void setEvent(final String event) {
		m_event = event;
	}

	/**
	 * @optional N/A
	 * @param event Event code- this can be passed in to filter the results to only contain films linked to this event.
	 * @see CineworldEvent
	 */
	public void setEvent(final CineworldEvent event) {
		setEvent(event == null? null : event.getCode());
	}

	/**
	 * @optional N/A
	 * @return Distributor id- this can be passed in to filter the results to only contain films from this distributor.
	 * @see CineworldDistributor
	 */
	public Integer getDistributor() {
		return m_distributor;
	}

	/**
	 * @optional N/A
	 * @param distributor Distributor id- this can be passed in to filter the results to only contain films from this
	 *            distributor.
	 * @see CineworldDistributor
	 */
	public void setDistributor(final Integer distributor) {
		m_distributor = distributor;
	}

	/**
	 * @optional N/A
	 * @param distributor Distributor id- this can be passed in to filter the results to only contain films from this
	 *            distributor.
	 * @see CineworldDistributor
	 */
	public void setDistributor(final CineworldDistributor distributor) {
		setDistributor(distributor == null? null : distributor.getId());
	}
}
