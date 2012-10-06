package com.twister.cineworld.db;

import java.util.List;

import android.database.DatabaseUtils;
import android.database.sqlite.*;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.Cinema;

class DataBaseWriter {
	private static final Log		LOG	= LogFactory.getLog(Tag.DB);

	/* Queries at the end */

	private final DataBaseHelper	m_dataBaseHelper;
	private SQLiteDatabase			m_database;

	public DataBaseWriter(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	/* Statements */
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
			DataBaseWriter.LOG.debug(String.format("Inserting cinema: %d, %d, %s, %s",
					cinema.getCompanyId(), cinema.getId(), cinema.getName(), cinema.getPostcode()));
			SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
			prepareStatements(database);
			database.beginTransaction();
			int column = 0;
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getCompanyId());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getId());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getName());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getDetailsUrl());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getTerritory());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getAddress());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getPostcode());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getTelephone());
			GeoPoint location = cinema.getLocation();
			if (location != null) {
				DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, location.getLatitudeE6());
				DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, location.getLongitudeE6());
			} else {
				m_insertCinema.bindNull(++column);
				m_insertCinema.bindNull(++column);
			}
			long cinemaID;
			try {
				cinemaID = m_insertCinema.executeInsert();
			} catch (SQLiteConstraintException ex) {
				DataBaseWriter.LOG.warn(ex, "Cannot insert cinema, getting existing (%d, %s)",
						cinema.getCompanyId(), cinema.getName());
				cinemaID = getCinemaID(cinema.getCompanyId(), cinema.getName());
			}
			cinema.setId((int) cinemaID);
			database.setTransactionSuccessful();
		} finally {
			m_database.endTransaction();
		}
	}

	private long getCinemaID(final int companyId, final String cinemaName) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		return DatabaseUtils.longForQuery(database, "SELECT _id FROM Cinema WHERE _company = ? AND name = ?",
				new String[] { String.valueOf(companyId), cinemaName });
	}

	// @formatter:off
	private static final String	 SQL_INSERT_CINEMA	= "INSERT INTO "
			+ "Cinema(_company, _id, name, details_url, territory, address, postcode, telephone, latitude, longitude) "
			+ "VALUES(       ?,   ?,    ?,           ?,         ?,       ?,        ?,         ?,        ?,         ?);";
	// @formatter:on
}
