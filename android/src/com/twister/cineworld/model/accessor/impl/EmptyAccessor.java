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

	public List<Cinema> getCinemas(final int filmEdi) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getAllFilms() throws ApplicationException {
		return Collections.emptyList();
	}

	public Film getFilm(final int filmEdi) throws ApplicationException {
		return null;
	}

	public List<Film> getFilms(final int cinemaId) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Film> getFilms(final int cinemaId, final TimeSpan span) throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Date> getAllDates() throws ApplicationException {
		return Collections.emptyList();
	}

	public List<Date> getDates(final int filmEdi) throws ApplicationException {
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

	public List<Performance> getPeformances(final int cinemaId, final int filmEdi, final int date)
			throws ApplicationException {
		return Collections.emptyList();
	}
}
