package com.twister.cineworld.db;

import java.util.*;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.*;

import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.tools.DBTools;

@SuppressWarnings("unused")
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
	private SQLiteStatement	m_insertGeoCache;
	private SQLiteStatement	m_updateGeoCache;

	private SQLiteDatabase prepareDB() {
		SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
		prepareStatements(database);
		return database;
	}

	private void prepareStatements(final SQLiteDatabase database) {
		if (m_database != database) {
			LOG.info("Preparing statements %s -> %s", DBTools.toString(m_database), DBTools.toString(database));
			m_database = database;

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

			if (m_insertGeoCache != null) {
				m_insertGeoCache.close();
			}
			m_insertGeoCache = database.compileStatement(SQL_INSERT_GEOCACHE);

			if (m_updateGeoCache != null) {
				m_updateGeoCache.close();
			}
			m_updateGeoCache = database.compileStatement(SQL_UPDATE_GEOCACHE);
		}
	}

	// #endregion

	// #region insert*
	public void insertCinemas(final List<Cinema> cinemas) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Cinema cinema : cinemas) {
				insertCinema(cinema);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	public void insertCinema(final Cinema cinema) {
		LOG.debug("Inserting cinema: %d, %d, %s, %s",
				cinema.getCompanyId(), cinema.getId(), cinema.getName(), cinema.getPostcode());
		SQLiteDatabase database = prepareDB();
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getCompanyId());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getId());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getName());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getDetailsUrl());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getTerritory());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getAddress());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getPostcode());
		DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, cinema.getTelephone());
		Location location = cinema.getLocation();
		if (location != null) {
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, location.getLatitude());
			DatabaseUtils.bindObjectToProgram(m_insertCinema, ++column, location.getLongitude());
		} else {
			m_insertCinema.bindNull(++column);
			m_insertCinema.bindNull(++column);
		}
		long cinemaID;
		try {
			cinemaID = m_insertCinema.executeInsert();
			cinema.setLastUpdate(Calendar.getInstance());
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert cinema, getting existing (%d, %s)", ex,
					cinema.getCompanyId(), cinema.getName());
			cinemaID = getCinemaID(cinema.getCompanyId(), cinema.getName());
		}
		cinema.setId((int) cinemaID);
	}

	private long getCinemaID(final int companyId, final String cinemaName) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		return DatabaseUtils.longForQuery(database, "SELECT _id FROM Cinema WHERE _company = ? AND name = ?",
				new String[] { String.valueOf(companyId), cinemaName });
	}

	public void insertCategories(final List<Category> categories) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Category category : categories) {
				insertCategory(category);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void insertCategory(final Category category) {
		LOG.debug("Inserting category: %s, %s", category.getCode(), category.getName());
		SQLiteDatabase database = prepareDB();
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertCategory, ++column, category.getCode());
		DatabaseUtils.bindObjectToProgram(m_insertCategory, ++column, category.getName());
		try {
			m_insertCategory.executeInsert();
			category.setLastUpdate(Calendar.getInstance());
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert category, ignoring", ex);
		}
	}

	public void insertEvents(final List<Event> events) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Event event : events) {
				insertEvent(event);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void insertEvent(final Event event) {
		LOG.debug("Inserting event: %s, %s", event.getCode(), event.getName());
		SQLiteDatabase database = prepareDB();
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertEvent, ++column, event.getCode());
		DatabaseUtils.bindObjectToProgram(m_insertEvent, ++column, event.getName());
		try {
			m_insertEvent.executeInsert();
			event.setLastUpdate(Calendar.getInstance());
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert event, ignoring", ex);
		}
	}

	public void insertDistributors(final List<Distributor> distributors) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Distributor distributor : distributors) {
				insertDistributor(distributor);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void insertDistributor(final Distributor distributor) {
		LOG.debug("Inserting distributor: %d, %s", distributor.getId(), distributor.getName());
		SQLiteDatabase database = prepareDB();
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertDistributor, ++column, distributor.getId());
		DatabaseUtils.bindObjectToProgram(m_insertDistributor, ++column, distributor.getName());
		try {
			m_insertDistributor.executeInsert();
			distributor.setLastUpdate(Calendar.getInstance());
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert distributor, ignoring", ex);
		}
	}

	public void updateGeoCache(final Iterable<PostCodeLocation> locations) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (PostCodeLocation location : locations) {
				updateGeoCache(location);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	public void updateGeoCache(final PostCodeLocation location) {
		LOG.debug("Updating location: %s to %s", location.getPostCode(), location.getLocation());
		SQLiteDatabase database = prepareDB();
		try {
			ContentValues values = new ContentValues(2);
			values.put("latitude", location.getLocation().getLatitude());
			values.put("longitude", location.getLocation().getLongitude());
			int rows = database.update("\"Helper:GeoCache\"", values,
					"_postcode = ?", new String[] { location.getPostCode() });
			if (rows == 0) {
				insertGeoCache(location);
			}
			location.setLastUpdate(Calendar.getInstance());
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot update location, ignoring", ex);
		}
	}

	public void insertGeoCache(final PostCodeLocation location) {
		LOG.debug("Inserting location: %s, %s", location.getPostCode(), location.getLocation());
		SQLiteDatabase database = prepareDB();
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertGeoCache, ++column, location.getPostCode());
		DatabaseUtils.bindObjectToProgram(m_insertGeoCache, ++column, location.getLocation().getLatitude());
		DatabaseUtils.bindObjectToProgram(m_insertGeoCache, ++column, location.getLocation().getLongitude());
		try {
			m_insertGeoCache.executeInsert();
			location.setLastUpdate(Calendar.getInstance());
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert location, ignoring", ex);
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
	private static final String	 SQL_INSERT_GEOCACHE = "INSERT INTO "
			+ "\"Helper:GeoCache\"(postcode, latitude, longitude) "
			+ "VALUES(                    ?,        ?,         ?);";
	private static final String	 SQL_UPDATE_GEOCACHE = "UPDATE "
			+ "\"Helper:GeoCache\""
			+ "SET latitude = ?, longitude = ?"
			+ "WHERE postcode = ?;";
	
	// #endnoformat

	// #endregion
}
