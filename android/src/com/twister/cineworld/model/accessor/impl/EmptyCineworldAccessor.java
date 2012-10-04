package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.accessor.CineworldAccessor;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.generic.Date;
import com.twister.cineworld.model.json.TimeSpan;

public class EmptyCineworldAccessor implements CineworldAccessor {
	public List<Cinema> getAllCinemas() throws CineworldException {
		return Collections.emptyList();
	}

	public Cinema getCinema(final int cinemaId) throws CineworldException {
		return null;
	}

	public List<Cinema> getCinemas(final int filmEdi) throws CineworldException {
		return Collections.emptyList();
	}

	public List<Film> getAllFilms() throws CineworldException {
		return Collections.emptyList();
	}

	public Film getFilm(final int filmEdi) throws CineworldException {
		return null;
	}

	public List<Film> getFilms(final int cinemaId) throws CineworldException {
		return Collections.emptyList();
	}

	public List<Film> getFilms(final int cinemaId, final TimeSpan span) throws CineworldException {
		return Collections.emptyList();
	}

	public List<Date> getAllDates() throws CineworldException {
		return Collections.emptyList();
	}

	public List<Date> getDates(final int filmEdi) throws CineworldException {
		return Collections.emptyList();
	}

	public List<Category> getAllCategories() throws CineworldException {
		return Collections.emptyList();
	}

	public List<Event> getAllEvents() throws CineworldException {
		return Collections.emptyList();
	}

	public List<Distributor> getAllDistributors() throws CineworldException {
		return Collections.emptyList();
	}

	public List<Performance> getPeformances(final int cinemaId, final int filmEdi, final int date)
			throws CineworldException {
		return Collections.emptyList();
	}
}
