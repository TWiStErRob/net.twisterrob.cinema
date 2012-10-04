package com.twister.cineworld.db;

import java.util.*;

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.Cinema;

/**
 * See details in the docs under <SDK_DIR>/docs/guide/topics/data/data- storage.html and I think you'll find the answer
 * You should implement SQLiteDatabase.CursorFactory interface, and create an SQLiteDatabase instance by calling the
 * static method SQLiteDatabase.openOrCreateDatabase. Details info of SQLiteDatabase.CursorFactory is in <SDK_DIR>/docs/
 * reference/android/m_database/sqlite/SQLiteDatabase.CursorFactory.html
 */
public class DataBaseHelper {
	private static final CineworldLogger	LOG	= LogFactory.getLog(Tag.DB);
	private final DataBaseOpenHelper		m_helper;
	private final DataBaseWriter			m_writer;
	private final DataBaseReader			m_reader;

	public DataBaseHelper(final Context context) {
		m_helper = new DataBaseOpenHelper(context);
		m_reader = new DataBaseReader();
		m_writer = new DataBaseWriter();
	}

	SQLiteDatabase getReadableDatabase() {
		return m_helper.getReadableDatabase();
	}

	SQLiteDatabase getWritableDatabase() {
		return m_helper.getWritableDatabase();
	}

	public void addCinemas(final List<Cinema> cinemas) {
		m_writer.insertCinemas(cinemas);
	}

	public List<Cinema> getCinemas() {
		return m_reader.getCinemas();
	}

	public Cinema getCinema(final int cinemaId) {
		return m_reader.getCinema(cinemaId);
	}

	private class DataBaseWriter {
		private SQLiteDatabase	m_db;

		private SQLiteStatement	m_insertCinema;

		private void prepareStatements(final SQLiteDatabase db) {
			if (db != this.m_db) {
				this.m_db = db;
				if (m_insertCinema != null) {
					m_insertCinema.close();
				}
				m_insertCinema = db
						.compileStatement("INSERT INTO Cinema(_id, name, postcode, latitude, longitude) VALUES(?, ?, ?, ?, ?);");
			}
		}

		public void insertCinemas(final List<Cinema> cinemas) {
			for (Cinema cinema : cinemas) {
				insertCinema(cinema);
			}
		}

		public void insertCinema(final Cinema cinema) {
			SQLiteDatabase database = getWritableDatabase();
			prepareStatements(database);
			try {
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
					DataBaseHelper.LOG.warn(ex, "Cannot insert cinema, getting existing");
					cinemaID = m_reader.getCinemaID(cinema.getName());
				}
				cinema.setId((int) cinemaID);
				// Log.debug(String.format("Inserting route %s -> %d", route.name, routeID));
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}
	}

	private class DataBaseReader {
		private final String[]	CINEMA_DETAILS	= { "_id", "name", "postcode", "latitude", "longitude" };

		public long getCinemaID(final String name) {
			SQLiteDatabase database = getReadableDatabase();
			return DatabaseUtils.longForQuery(database, "SELECT _id FROM Cinema WHERE name = ?", new String[] { name });
		}

		public List<Cinema> getCinemas() {
			List<Cinema> cinemas = new ArrayList<Cinema>();
			SQLiteDatabase database = getReadableDatabase();
			Cursor cursor = database.query("Cinema", CINEMA_DETAILS, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Cinema cinema = getCinema(cursor);
				cinemas.add(cinema);
			}
			cursor.close();
			return cinemas;
		}

		private Cinema getCinema(final Cursor cursor) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String postcode = cursor.getString(cursor.getColumnIndex("postcode"));
			int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
			int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));

			Cinema cinema = new Cinema();
			cinema.setId(id);
			cinema.setName(name);
			cinema.setPostcode(postcode);
			cinema.setLocation(new GeoPoint(latitude, longitude));
			return cinema;
		}

		@SuppressWarnings("unused")
		public int getNumberOfCinemas() {
			SQLiteDatabase db = getReadableDatabase();
			int entries = (int) DatabaseUtils.queryNumEntries(db, "Cinema");
			return entries;
		}

		public Cinema getCinema(final int cinemaId) {
			SQLiteDatabase database = getReadableDatabase();
			Cursor cursor = database.query("Cinema", CINEMA_DETAILS, "_id = ?",
					new String[] { String.valueOf(cinemaId) }, null, null,
					null);
			Cinema cinema = null;
			if (cursor.moveToNext()) {
				cinema = getCinema(cursor);
			}
			cursor.close();
			return cinema;
		}
	}

}
