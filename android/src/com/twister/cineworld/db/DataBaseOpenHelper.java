package com.twister.cineworld.db;

import java.io.*;

import android.content.Context;
import android.database.sqlite.*;

import com.twister.cineworld.App;
import com.twister.cineworld.log.*;
import com.twister.cineworld.tools.IOTools;

class DataBaseOpenHelper extends SQLiteOpenHelper {
	private static final String				DB_SCHEMA_FILE	= "CineworldExtra.v1.sql";
	private static final String				DB_NAME			= "CineworldExtra";
	private static final int				DB_VERSION		= 1;
	private static final CineworldLogger	LOGGER			= LogFactory.getLog(Tag.DB);

	public DataBaseOpenHelper(final Context context) {
		super(context, DataBaseOpenHelper.DB_NAME, null, DataBaseOpenHelper.DB_VERSION);
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		DataBaseOpenHelper.LOGGER.debug("Opening database: " + db);
		super.onOpen(db);
		DataBaseOpenHelper.LOGGER.info("Opened database: " + db);
		// db.execSQL("DELETE FROM Route WHERE direction IS NULL;");
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		DataBaseOpenHelper.LOGGER.debug("Creating database: " + db);
		DataBaseOpenHelper.execFile(db, DataBaseOpenHelper.DB_SCHEMA_FILE);
		DataBaseOpenHelper.LOGGER.info("Created database: " + db);
	}

	private static void execFile(final SQLiteDatabase db, final String dbSchemaFile) {
		DataBaseOpenHelper.LOGGER.debug("Executing file " + dbSchemaFile + " into database: " + db);
		InputStream s;
		try {
			s = App.getInstance().getAssets().open(dbSchemaFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(s, IOTools.ENCODING));
			String statement = null;
			while ((statement = DataBaseOpenHelper.getNextStatement(reader)) != null) {
				db.execSQL(statement);
			}
		} catch (IOException e) {
			DataBaseOpenHelper.LOGGER.error("Error creating database from file: "
					+ DataBaseOpenHelper.DB_SCHEMA_FILE, e);
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
			if (line.matches(";\\s*$")) {
				return sb.toString();  // ends in a semicolon -> end of statement
			}
		}
		return null;
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		// if (oldVersion < 2 && 2 <= newVersion)
	}
}
