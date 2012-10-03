package com.twister.cineworld.model.accessor.impl;

import java.util.List;

import com.twister.cineworld.App;
import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.generic.Cinema;
import com.twister.cineworld.model.json.data.CineworldCinema;

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
	public CineworldCinema getCinema(final int cinemaId) throws CineworldException {
		// TODO Auto-generated method stub
		return null;
	}
}
