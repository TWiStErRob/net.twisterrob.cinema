package com.twister.cineworld.db;

import java.io.*;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

import com.twister.cineworld.*;
import com.twister.cineworld.log.*;
import com.twister.cineworld.tools.IOTools;

class DataBaseOpenHelper extends SQLiteOpenHelper {
	private static final String			DB_SCHEMA_FILE	= "CineworldExtra.v1.schema.sql";
	private static final String			DB_DATA_FILE	= "CineworldExtra.v1.data.sql";
	private static final String			DB_CLEAN_FILE	= "CineworldExtra.v1.clean.sql";
	private static final String			DB_NAME			= "CineworldExtra";
	private static final int			DB_VERSION		= 12;
	private static final Log			LOG				= LogFactory.getLog(Tag.DB);
	private static final CursorFactory	s_factory		= new LoggingCursorFactory();

	public DataBaseOpenHelper(final Context context) {
		super(context, DB_NAME, s_factory, DB_VERSION);
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		LOG.debug("Opening database: %s", db);
		// onCreate(db); // for DB development, always clear and initialize
		if (BuildConfig.DEBUG) {
			try {
				String target = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ "CineworldDB.sqlite";
				IOTools.copyFile(db.getPath(), target);
				LOG.info("DB backed up to %s", target);
			} catch (IOException ex) {
				LOG.error("Cannot back up DB on open", ex);
			}
		}
		super.onOpen(db);
		LOG.info("Opened database: %s", db);
		// db.execSQL("DELETE FROM Cinema;");
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		LOG.debug("Creating database: %s", db);
		DataBaseOpenHelper.execFile(db, DB_CLEAN_FILE);
		DataBaseOpenHelper.execFile(db, DB_SCHEMA_FILE);
		DataBaseOpenHelper.execFile(db, DB_DATA_FILE);
		LOG.info("Created database: %s", db);
	}

	private static void execFile(final SQLiteDatabase db, final String dbSchemaFile) {
		LOG.debug("Executing file %s into database: %s", dbSchemaFile, db);
		InputStream s;
		String statement = null;
		try {
			s = App.getInstance().getAssets().open(dbSchemaFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(s, IOTools.ENCODING));
			while ((statement = DataBaseOpenHelper.getNextStatement(reader)) != null) {
				db.execSQL(statement);
			}
		} catch (SQLException ex) {
			LOG.error("Error creating database from file: %s  while executing\n%s", ex,
					dbSchemaFile, statement);
		} catch (IOException ex) {
			LOG.error("Error creating database from file: ", ex, dbSchemaFile);
		}
	}

	private static String getNextStatement(final BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.matches("\\s*")) {
				continue; // empty lines
			}
			sb.append(line);
			sb.append(IOTools.LINE_SEPARATOR);
			if (line.matches(".*;\\s*$")) {
				return sb.toString();  // ends in a semicolon -> end of statement
			}
		}
		return null;
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		onCreate(db);
	}
}
