package com.twister.cineworld.model.accessor.impl;

import java.util.List;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.generic.Cinema;

public class DBCineworldAccessor extends EmptyCineworldAccessor {
	private DataBaseHelper	m_db;

	public DBCineworldAccessor() {
		m_db = App.getInstance().getDataBaseHelper();
	}

	@Override
	public List<Cinema> getAllCinemas() throws CineworldException {
		return m_db.getCinemas();
	}

	@Override
	public Cinema getCinema(final int cinemaId) throws CineworldException {
		return m_db.getCinema(cinemaId);
	}
}
