package com.twister.cineworld.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * See details in the docs under <SDK_DIR>/docs/guide/topics/data/data- storage.html and I think you'll find the answer
 * You should implement SQLiteDatabase.CursorFactory interface, and create an SQLiteDatabase instance by calling the
 * static method SQLiteDatabase.openOrCreateDatabase. Details info of SQLiteDatabase.CursorFactory is in <SDK_DIR>/docs/
 * reference/android/m_database/sqlite/SQLiteDatabase.CursorFactory.html
 */
public class DataBaseHelper {
	final Context						m_context;
	private final DataBaseOpenHelper	m_helper;

	public DataBaseHelper(final Context context) {
		m_context = context;
		m_helper = new DataBaseOpenHelper(m_context);
	}

	public SQLiteDatabase getReadableDatabase() {
		return m_helper.getReadableDatabase();
	}

	public SQLiteDatabase getWritableDatabase() {
		return m_helper.getWritableDatabase();
	}
}
