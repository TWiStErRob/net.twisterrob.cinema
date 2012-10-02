package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.accessor.CineworldAccessor;
import com.twister.cineworld.model.generic.Cinema;
import com.twister.cineworld.model.json.TimeSpan;
import com.twister.cineworld.model.json.data.*;

public class TrivialDBCacheCineworldAccessor implements CineworldAccessor {
	private static final CineworldLogger	LOG	= LogFactory.getLog(Tag.ACCESS);
	private DataBaseHelper					m_dbh;
	private CineworldAccessor				m_dbAccessor;
	private CineworldAccessor				m_otherAccessor;

	public TrivialDBCacheCineworldAccessor(final CineworldAccessor dbAccessor, final CineworldAccessor otherAccessor) {
		m_dbh = App.getInstance().getDataBaseHelper();
		m_dbAccessor = dbAccessor;
		m_otherAccessor = otherAccessor;
	}

	public List<Cinema> getAllCinemas() throws CineworldException {
		List<Cinema> cinemas = m_dbAccessor.getAllCinemas();
		if (cinemas.isEmpty()) {
			TrivialDBCacheCineworldAccessor.LOG.debug("No data in DB, requesting cinemas...");
			cinemas = m_otherAccessor.getAllCinemas();
			TrivialDBCacheCineworldAccessor.LOG.debug("No data in DB, inserting " + cinemas.size() + " cinemas...");
			m_dbh.addCinemas(cinemas);
			TrivialDBCacheCineworldAccessor.LOG.debug("Returning from other source: " + cinemas.size() + " cinemas...");
		} else {
			TrivialDBCacheCineworldAccessor.LOG.debug("Returning from DB: " + cinemas.size() + " cinemas...");
		}
		return cinemas;
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
