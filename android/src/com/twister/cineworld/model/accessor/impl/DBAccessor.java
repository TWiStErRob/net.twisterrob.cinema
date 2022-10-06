package com.twister.cineworld.model.accessor.impl;

import java.util.List;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;

public class DBAccessor extends EmptyAccessor {
	private DataBaseHelper	m_db;

	public DBAccessor() {
		m_db = App.getInstance().getDataBaseHelper();
	}

	@Override
	public List<Cinema> getAllCinemas() throws ApplicationException {
		return m_db.getCinemas();
	}

	@Override
	public Cinema getCinema(final int cinemaId) throws ApplicationException {
		return m_db.getCinema(cinemaId);
	}

	@Override
	public List<Category> getAllCategories() throws ApplicationException {
		return m_db.getCategories();
	}

	@Override
	public List<Event> getAllEvents() throws ApplicationException {
		return m_db.getEvents();
	}

	@Override
	public List<Distributor> getAllDistributors() throws ApplicationException {
		return m_db.getDistributors();
	}
}
