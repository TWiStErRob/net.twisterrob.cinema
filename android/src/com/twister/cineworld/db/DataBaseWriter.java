package com.twister.cineworld.db;

import java.util.List;

import android.database.DatabaseUtils;
import android.database.sqlite.*;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.*;

class DataBaseWriter {
	private static final Log		LOG	= LogFactory.getLog(Tag.DB);

	/* Queries at the end */

	private final DataBaseHelper	m_dataBaseHelper;
	private SQLiteDatabase			m_database;

	public DataBaseWriter(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	// #region Prepared statements
	private SQLiteStatement	m_insertCinema;
	private SQLiteStatement	m_insertCategory;
	private SQLiteStatement	m_insertEvent;
	private SQLiteStatement	m_insertDistributor;

	private void prepareStatements(final SQLiteDatabase database) {
		if (this.m_database != database) {
			this.m_database = database;

			if (m_insertCinema != null) {
				m_insertCinema.close();
			}
			m_insertCinema = database.compileStatement(SQL_INSERT_CINEMA);

			if (m_insertCategory != null) {
				m_insertCategory.close();
			}
			m_insertCategory = database.compileStatement(SQL_INSERT_CATEGORY);

			if (m_insertEvent != null) {
				m_insertEvent.close();
			}
			m_insertEvent = database.compileStatement(SQL_INSERT_EVENT);

			if (m_insertDistributor != null) {
				m_insertDistributor.close();
			}
			m_insertDistributor = database.compileStatement(SQL_INSERT_DISTRIBUTOR);
		}
	}

	// #endregion

	// #region insert*
	public void insertCinemas(final List<Cinema> cinemas) {
		for (Cinema cinema : cinemas) {
			insertCinema(cinema);
		}
	}

	public void insertCinema(final Cinema cinema) {
		try {
			LOG.debug("Inserting cinema: %d, %d, %s, %s",
					cinema.getCompanyId(), cinema.getId(), cinema.getName(), cinema.getPostcode());
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
				LOG.warn("Cannot insert cinema, getting existing (%d, %s)", ex,
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

	public void insertCategories(final List<Category> categories) {
		for (Category category : categories) {
			insertCategory(category);
		}
	}

	private void insertCategory(final Category category) {
		try {
			LOG.debug("Inserting category: %s, %s", category.getCode(), category.getName());
			SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
			prepareStatements(database);
			database.beginTransaction();
			int column = 0;
			DatabaseUtils.bindObjectToProgram(m_insertCategory, ++column, category.getCode());
			DatabaseUtils.bindObjectToProgram(m_insertCategory, ++column, category.getName());
			try {
				m_insertCategory.executeInsert();
			} catch (SQLiteConstraintException ex) {
				LOG.warn("Cannot insert category, ignoring", ex);
			}
			database.setTransactionSuccessful();
		} finally {
			m_database.endTransaction();
		}
	}

	public void insertEvents(final List<Event> events) {
		for (Event event : events) {
			insertEvent(event);
		}
	}

	private void insertEvent(final Event event) {
		try {
			LOG.debug("Inserting event: %s, %s", event.getCode(), event.getName());
			SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
			prepareStatements(database);
			database.beginTransaction();
			int column = 0;
			DatabaseUtils.bindObjectToProgram(m_insertEvent, ++column, event.getCode());
			DatabaseUtils.bindObjectToProgram(m_insertEvent, ++column, event.getName());
			try {
				m_insertEvent.executeInsert();
			} catch (SQLiteConstraintException ex) {
				LOG.warn("Cannot insert event, ignoring", ex);
			}
			database.setTransactionSuccessful();
		} finally {
			m_database.endTransaction();
		}
	}

	public void insertDistributors(final List<Distributor> distributors) {
		for (Distributor distributor : distributors) {
			insertDistributor(distributor);
		}
	}

	private void insertDistributor(final Distributor distributor) {
		try {
			LOG.debug("Inserting distributor: %d, %s", distributor.getId(), distributor.getName());
			SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
			prepareStatements(database);
			database.beginTransaction();
			int column = 0;
			DatabaseUtils.bindObjectToProgram(m_insertDistributor, ++column, distributor.getId());
			DatabaseUtils.bindObjectToProgram(m_insertDistributor, ++column, distributor.getName());
			try {
				m_insertDistributor.executeInsert();
			} catch (SQLiteConstraintException ex) {
				LOG.warn("Cannot insert distributor, ignoring", ex);
			}
			database.setTransactionSuccessful();
		} finally {
			m_database.endTransaction();
		}
	}



	// #endregion

	// #region Queries

	// #noformat
	private static final String	 SQL_INSERT_CINEMA	= "INSERT INTO "
			+ "Cinema(_company, _id, name, details_url, territory, address, postcode, telephone, latitude, longitude) "
			+ "VALUES(       ?,   ?,    ?,           ?,         ?,       ?,        ?,         ?,        ?,         ?);";
	private static final String	 SQL_INSERT_CATEGORY= "INSERT INTO "
			+ "FilmCategory(code, name) "
			+ "VALUES(         ?,    ?);";
	private static final String	 SQL_INSERT_EVENT	= "INSERT INTO "
			+ "Event(code, name) "
			+ "VALUES(         ?,    ?);";
	private static final String	 SQL_INSERT_DISTRIBUTOR	= "INSERT INTO "
			+ "FilmDistributor(_id, name) "
			+ "VALUES(           ?,    ?);";
	// #endnoformat

	// #endregion
}
