package com.twister.cineworld.db;

import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.twister.cineworld.log.*;

public final class LoggingCursorFactory implements CursorFactory {
	private static final Log	LOG	= LogFactory.getLog(Tag.DB);

	public Cursor newCursor(final SQLiteDatabase db,
			final SQLiteCursorDriver masterQuery,
			final String editTable, final SQLiteQuery query) {
		LoggingCursorFactory.LOG.verbose(query.toString());
		return new SQLiteCursor(db, masterQuery, editTable, query);
	}
}
