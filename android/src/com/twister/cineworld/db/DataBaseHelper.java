package com.twister.cineworld.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.twister.cineworld.model.generic.*;

/**
 * See details in the docs under <SDK_DIR>/docs/guide/topics/data/data- storage.html and I think you'll find the answer
 * You should implement SQLiteDatabase.CursorFactory interface, and create an SQLiteDatabase instance by calling the
 * static method SQLiteDatabase.openOrCreateDatabase. Details info of SQLiteDatabase.CursorFactory is in <SDK_DIR>/docs/
 * reference/android/m_database/sqlite/SQLiteDatabase.CursorFactory.html
 */
public class DataBaseHelper {
	private final DataBaseOpenHelper	m_helper;
	private final DataBaseWriter		m_writer;
	private final DataBaseReader		m_reader;

	public DataBaseHelper(final Context context) {
		m_helper = new DataBaseOpenHelper(context);
		m_reader = new DataBaseReader(this);
		m_writer = new DataBaseWriter(this);
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

	public void openDB() {
		getWritableDatabase();
	}

	public List<PostCodeLocation> getGeoCacheLocations() {
		return m_reader.getGeoCache();
	}
}
