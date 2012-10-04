package com.twister.cineworld.model.accessor.impl;

import java.util.List;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Cinema;

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
}
