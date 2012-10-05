package com.twister.cineworld.db;

import java.util.List;

import android.database.DatabaseUtils;
import android.database.sqlite.*;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.Cinema;

class DataBaseWriter {
	private static final CineworldLogger	LOG					= LogFactory.getLog(Tag.DB);

	private static final String				SQL_INSERT_CINEMA	= "INSERT INTO "
																		+ "Cinema(_id, name, postcode, latitude, longitude) "
																		+ "VALUES(?, ?, ?, ?, ?);";

	private final DataBaseHelper			m_dataBaseHelper;

	public DataBaseWriter(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	private SQLiteDatabase	m_database;

	private SQLiteStatement	m_insertCinema;

	private void prepareStatements(final SQLiteDatabase database) {
		if (this.m_database != database) {
			this.m_database = database;
			if (m_insertCinema != null) {
				m_insertCinema.close();
			}
			m_insertCinema = database.compileStatement(DataBaseWriter.SQL_INSERT_CINEMA);
		}
	}

	public void insertCinemas(final List<Cinema> cinemas) {
		for (Cinema cinema : cinemas) {
			insertCinema(cinema);
		}
	}

	public void insertCinema(final Cinema cinema) {
		try {
			SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
			prepareStatements(database);
			database.beginTransaction();
			DatabaseUtils.bindObjectToProgram(m_insertCinema, 1, cinema.getId());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, 2, cinema.getName());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, 3, cinema.getPostcode());
			GeoPoint location = cinema.getLocation();
			if (location != null) {
				DatabaseUtils.bindObjectToProgram(m_insertCinema, 4, location.getLatitudeE6());
				DatabaseUtils.bindObjectToProgram(m_insertCinema, 5, location.getLongitudeE6());
			} else {
				m_insertCinema.bindNull(4);
				m_insertCinema.bindNull(5);
			}
			long cinemaID;
			try {
				cinemaID = m_insertCinema.executeInsert();
			} catch (SQLiteConstraintException ex) {
				DataBaseWriter.LOG.warn(ex, "Cannot insert cinema, getting existing");
				cinemaID = getCinemaID(cinema.getName());
			}
			cinema.setId((int) cinemaID);
			// Log.debug(String.format("Inserting route %s -> %d", route.name, routeID));
			database.setTransactionSuccessful();
		} finally {
			m_database.endTransaction();
		}
	}

	private long getCinemaID(final String name) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		return DatabaseUtils.longForQuery(database, "SELECT _id FROM Cinema WHERE name = ?", new String[] { name });
	}
}
