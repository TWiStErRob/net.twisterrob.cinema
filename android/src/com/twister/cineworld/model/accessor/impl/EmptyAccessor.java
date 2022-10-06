package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.generic.Date;
import com.twister.cineworld.model.json.TimeSpan;

public class EmptyAccessor implements Accessor {
	public List<Cinema> getAllCinemas() throws ApplicationException {
		return Collections.emptyList();
	}

	public Cinema getCinema(final int cinemaId) throws ApplicationException {
		return null;
	}

	public List<Cinema> getCinemasForFilm(final int filmEdi) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Cinema> getCinemasForDistributor(final int distributorId) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Cinema> getCinemasForEvent(final String eventCode) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Cinema> getCinemasForCategory(final String categoryCode) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Cinema> getCinemasForDate(final Calendar calendar) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getAllFilms() throws ApplicationException {
		return Collections.emptyList();
	}

	public Film getFilm(final int filmEdi) throws ApplicationException {
		return null;
	}

	public List<Film> getFilmsForCinema(final int cinemaId) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getFilmsForCinema(final int cinemaId, final TimeSpan span) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getFilmsForDistributor(final int distributorId) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getFilmsForEvent(final String eventCode) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getFilmsForCategory(final String categoryCode) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getFilmsForDate(final Calendar calendar) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Date> getAllDates() throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Date> getDatesForFilm(final int filmEdi) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Date> getDatesForCinema(final int cinemaId) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Date> getDatesForFilmAtCinema(int filmEdi, int cinemaId) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Category> getAllCategories() throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Event> getAllEvents() throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Distributor> getAllDistributors() throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Performance> getPeformances(final int cinemaId, final int filmEdi, final String date)
			throws ApplicationException {
		return Collections.emptyList();
	}
}
