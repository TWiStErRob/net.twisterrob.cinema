package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.generic.Cinema;

public class TrivialDBCacheAccessor extends DelegatingAccessor {
	private static final Log	LOG	= LogFactory.getLog(Tag.ACCESS);
	private DataBaseHelper		m_dbh;
	private Accessor			m_dbAccessor;
	private Accessor			m_otherAccessor;

	public TrivialDBCacheAccessor(final Accessor dbAccessor, final Accessor otherAccessor) {
		super(otherAccessor);
		m_dbh = App.getInstance().getDataBaseHelper();
		m_dbAccessor = dbAccessor;
		m_otherAccessor = otherAccessor;
	}

	@Override
	public List<Cinema> getAllCinemas() throws ApplicationException {
		List<Cinema> cinemas = m_dbAccessor.getAllCinemas();
		if (cinemas.isEmpty()) {
			TrivialDBCacheAccessor.LOG.debug("No data in DB, requesting cinemas...");
			cinemas = m_otherAccessor.getAllCinemas();
			TrivialDBCacheAccessor.LOG.debug("No data in DB, inserting " + cinemas.size() + " cinemas...");
			m_dbh.addCinemas(cinemas);
			TrivialDBCacheAccessor.LOG.debug("Returning from other source: " + cinemas.size() + " cinemas...");
		} else {
			TrivialDBCacheAccessor.LOG.debug("Returning from DB: " + cinemas.size() + " cinemas...");
		}
		return cinemas;
	}

	@Override
	public Cinema getCinema(final int cinemaId) throws ApplicationException {
		Cinema cinema = m_dbAccessor.getCinema(cinemaId);
		if (cinema == null) {
			TrivialDBCacheAccessor.LOG.debug("No data in DB, requesting cinemas...");
			cinema = m_otherAccessor.getCinema(cinemaId);
			TrivialDBCacheAccessor.LOG.debug("No data in DB, inserting a new cinema: " + cinema);
			m_dbh.addCinemas(Collections.singletonList(cinema));
			TrivialDBCacheAccessor.LOG.debug("Returning from other source: " + cinema);
		} else {
			TrivialDBCacheAccessor.LOG.debug("Returning from DB: " + cinema);
		}
		return cinema;
	}

	@Override
	public List<Cinema> getCinemas(final int filmEdi) throws ApplicationException {
		List<Cinema> cinemas = m_dbAccessor.getCinemas(filmEdi);
		if (cinemas.isEmpty()) {
			TrivialDBCacheAccessor.LOG.debug("No data in DB, requesting cinemas...");
			cinemas = m_otherAccessor.getCinemas(filmEdi);
			TrivialDBCacheAccessor.LOG.debug("No data in DB, inserting " + cinemas.size() + " cinemas...");
			m_dbh.addCinemas(cinemas);
			TrivialDBCacheAccessor.LOG.debug("Returning from other source: " + cinemas.size() + " cinemas...");
		} else {
			TrivialDBCacheAccessor.LOG.debug("Returning from DB: " + cinemas.size() + " cinemas...");
		}
		return cinemas;
	}
}
