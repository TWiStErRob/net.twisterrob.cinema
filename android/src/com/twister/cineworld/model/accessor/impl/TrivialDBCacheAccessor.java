package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.generic.*;

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
			LOG.debug("No data in DB, requesting cinemas...");
			cinemas = m_otherAccessor.getAllCinemas();
			LOG.debug("No data in DB, inserting %d cinemas...", cinemas.size());
			m_dbh.addCinemas(cinemas);
			LOG.debug("Returning from other source: %d cinemas...", cinemas.size());
		} else {
			LOG.debug("Returning from DB: %d cinemas...", cinemas.size());
		}
		return cinemas;
	}

	@Override
	public Cinema getCinema(final int cinemaId) throws ApplicationException {
		Cinema cinema = m_dbAccessor.getCinema(cinemaId);
		if (cinema == null) {
			LOG.debug("No data in DB, requesting cinemas...");
			cinema = m_otherAccessor.getCinema(cinemaId);
			LOG.debug("No data in DB, inserting a new cinema: %d", cinema);
			m_dbh.addCinemas(Collections.singletonList(cinema));
			LOG.debug("Returning from other source: %d", cinema);
		} else {
			LOG.debug("Returning from DB: %d", cinema);
		}
		return cinema;
	}

	@Override
	public List<Cinema> getCinemas(final int filmEdi) throws ApplicationException {
		List<Cinema> cinemas = m_dbAccessor.getCinemas(filmEdi);
		if (cinemas.isEmpty()) {
			LOG.debug("No data in DB, requesting cinemas...");
			cinemas = m_otherAccessor.getCinemas(filmEdi);
			LOG.debug("No data in DB, inserting %d cinemas...", cinemas.size());
			m_dbh.addCinemas(cinemas);
			LOG.debug("Returning from other source: %d cinemas...", cinemas.size());
		} else {
			LOG.debug("Returning from DB: %d cinemas...", cinemas.size());
		}
		return cinemas;
	}

	@Override
	public List<Category> getAllCategories() throws ApplicationException {
		List<Category> categories = m_dbAccessor.getAllCategories();
		if (categories.isEmpty()) {
			LOG.debug("No data in DB, requesting categories...");
			categories = m_otherAccessor.getAllCategories();
			LOG.debug("No data in DB, inserting %d categories...", categories.size());
			m_dbh.addCategories(categories);
			LOG.debug("Returning from other source: %d categories...", categories.size());
		} else {
			LOG.debug("Returning from DB: %d categories...", categories.size());
		}
		return categories;
	}

	@Override
	public List<Event> getAllEvents() throws ApplicationException {
		List<Event> events = m_dbAccessor.getAllEvents();
		if (events.isEmpty()) {
			LOG.debug("No data in DB, requesting events...");
			events = m_otherAccessor.getAllEvents();
			LOG.debug("No data in DB, inserting %d events...", events.size());
			m_dbh.addEvents(events);
			LOG.debug("Returning from other source: %d events...", events.size());
		} else {
			LOG.debug("Returning from DB: %d events...", events.size());
		}
		return events;
	}

	@Override
	public List<Distributor> getAllDistributors() throws ApplicationException {
		List<Distributor> distributors = m_dbAccessor.getAllDistributors();
		if (distributors.isEmpty()) {
			LOG.debug("No data in DB, requesting distributors...");
			distributors = m_otherAccessor.getAllDistributors();
			LOG.debug("No data in DB, inserting %d distributors...", distributors.size());
			m_dbh.addDistributors(distributors);
			LOG.debug("Returning from other source: %d distributors...", distributors.size());
		} else {
			LOG.debug("Returning from DB: %d distributors...", distributors.size());
		}
		return distributors;
	}
}
