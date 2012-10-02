package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.accessor.CineworldAccessor;
import com.twister.cineworld.model.json.TimeSpan;
import com.twister.cineworld.model.json.data.*;

public class DBCineworldAccessor implements CineworldAccessor {
	private DataBaseHelper	m_db;

	public DBCineworldAccessor() {
		m_db = App.getInstance().getDataBaseHelper();
		m_db.getWritableDatabase();
	}

	public List<CineworldCinema> getAllCinemas() throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public CineworldCinema getCinema(final int cinemaId) throws CineworldException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CineworldCinema> getCinemas(final int filmEdi) throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldFilm> getAllFilms() throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public CineworldFilm getFilm(final int filmEdi) throws CineworldException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CineworldFilm> getFilms(final int cinemaId) throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldFilm> getFilms(final int cinemaId, final TimeSpan span) throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldDate> getAllDates() throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldDate> getDates(final int filmEdi) throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldCategory> getAllCategories() throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldEvent> getAllEvents() throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldDistributor> getAllDistributors() throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	public List<CineworldPerformance> getPeformances(final int cinemaId, final int filmEdi, final int date)
			throws CineworldException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}
}
