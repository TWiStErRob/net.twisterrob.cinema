package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.generic.Date;
import com.twister.cineworld.model.json.TimeSpan;

public class DelegatingAccessor implements Accessor {
	private final Accessor	m_other;

	public DelegatingAccessor(final Accessor other) {
		m_other = other;
	}

	public List<Cinema> getAllCinemas() throws ApplicationException {
		return m_other.getAllCinemas();
	}

	public Cinema getCinema(final int cinemaId) throws ApplicationException {
		return m_other.getCinema(cinemaId);
	}

	public List<Cinema> getCinemasForFilm(final int filmEdi) throws ApplicationException {
		return m_other.getCinemasForFilm(filmEdi);
	}

	public List<Cinema> getCinemasForDistributor(final int distributorId) throws ApplicationException {
		return m_other.getCinemasForDistributor(distributorId);
	}

	public List<Cinema> getCinemasForEvent(final String eventCode) throws ApplicationException {
		return m_other.getCinemasForEvent(eventCode);
	}

	public List<Cinema> getCinemasForCategory(final String categoryCode) throws ApplicationException {
		return m_other.getCinemasForCategory(categoryCode);
	}

	public List<Cinema> getCinemasForDate(final Calendar calendar) throws ApplicationException {
		return m_other.getCinemasForDate(calendar);
	}

	public List<Film> getAllFilms() throws ApplicationException {
		return m_other.getAllFilms();
	}

	public Film getFilm(final int filmEdi) throws ApplicationException {
		return m_other.getFilm(filmEdi);
	}

	public List<Film> getFilmsForCinema(final int cinemaId) throws ApplicationException {
		return m_other.getFilmsForCinema(cinemaId);
	}

	public List<Film> getFilmsForCinema(final int cinemaId, final TimeSpan span) throws ApplicationException {
		return m_other.getFilmsForCinema(cinemaId, span);
	}

	public List<Film> getFilmsForDistributor(final int distributorId) throws ApplicationException {
		return m_other.getFilmsForDistributor(distributorId);
	}

	public List<Film> getFilmsForEvent(final String eventCode) throws ApplicationException {
		return m_other.getFilmsForEvent(eventCode);
	}

	public List<Film> getFilmsForCategory(final String categoryCode) throws ApplicationException {
		return m_other.getFilmsForCategory(categoryCode);
	}

	public List<Film> getFilmsForDate(final Calendar calendar) throws ApplicationException {
		return m_other.getFilmsForDate(calendar);
	}

	public List<Date> getAllDates() throws ApplicationException {
		return m_other.getAllDates();
	}

	public List<Date> getDatesForFilm(final int filmEdi) throws ApplicationException {
		return m_other.getDatesForFilm(filmEdi);
	}

	public List<Date> getDatesForCinema(final int cinemaId) throws ApplicationException {
		return m_other.getDatesForCinema(cinemaId);
	}

	public List<Category> getAllCategories() throws ApplicationException {
		return m_other.getAllCategories();
	}

	public List<Event> getAllEvents() throws ApplicationException {
		return m_other.getAllEvents();
	}

	public List<Distributor> getAllDistributors() throws ApplicationException {
		return m_other.getAllDistributors();
	}

	public List<Performance> getPeformances(final int cinemaId, final int filmEdi, final int date)
			throws ApplicationException {
		return m_other.getPeformances(cinemaId, filmEdi, date);
	}
}
