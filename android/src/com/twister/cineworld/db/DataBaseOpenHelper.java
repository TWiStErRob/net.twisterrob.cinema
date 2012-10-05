package com.twister.cineworld.db;

import java.io.*;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.*;

import com.twister.cineworld.App;
import com.twister.cineworld.log.*;
import com.twister.cineworld.tools.IOTools;

class DataBaseOpenHelper extends SQLiteOpenHelper {
	private static final String				DB_SCHEMA_FILE	= "CineworldExtra.v1.schema.sql";
	private static final String				DB_DATA_FILE	= "CineworldExtra.v1.data.sql";
	private static final String				DB_CLEAN_FILE	= "CineworldExtra.v1.clean.sql";
	private static final String				DB_NAME			= "CineworldExtra";
	private static final int				DB_VERSION		= 9;
	private static final CineworldLogger	LOGGER			= LogFactory.getLog(Tag.DB);

	public DataBaseOpenHelper(final Context context) {
		super(context, DataBaseOpenHelper.DB_NAME, null, DataBaseOpenHelper.DB_VERSION);
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		DataBaseOpenHelper.LOGGER.debug("Opening database: " + db);
		// onCreate(db); // for DB development, always clear and initialize
		super.onOpen(db);
		DataBaseOpenHelper.LOGGER.info("Opened database: " + db);
		// db.execSQL("DELETE FROM Cinema;");
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		DataBaseOpenHelper.LOGGER.debug("Creating database: " + db);
		DataBaseOpenHelper.execFile(db, DataBaseOpenHelper.DB_CLEAN_FILE);
		DataBaseOpenHelper.execFile(db, DataBaseOpenHelper.DB_SCHEMA_FILE);
		DataBaseOpenHelper.execFile(db, DataBaseOpenHelper.DB_DATA_FILE);
		DataBaseOpenHelper.LOGGER.info("Created database: " + db);
	}

	private static void execFile(final SQLiteDatabase db, final String dbSchemaFile) {
		DataBaseOpenHelper.LOGGER.debug("Executing file " + dbSchemaFile + " into database: " + db);
		InputStream s;
		String statement = null;
		try {
			s = App.getInstance().getAssets().open(dbSchemaFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(s, IOTools.ENCODING));
			while ((statement = DataBaseOpenHelper.getNextStatement(reader)) != null) {
				db.execSQL(statement);
			}
		} catch (SQLException e) {
			DataBaseOpenHelper.LOGGER.error("Error creating database from file: "
					+ dbSchemaFile + " while executing\n" + statement, e);
		} catch (IOException e) {
			DataBaseOpenHelper.LOGGER.error("Error creating database from file: "
					+ dbSchemaFile, e);
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
