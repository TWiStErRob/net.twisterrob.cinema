package com.twister.cineworld.db;

import java.util.*;

import android.database.*;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.model.generic.*;

class DataBaseReader {
	private static final String		GENERIC_SOURCE		= "DB";
	// private static final CineworldLogger LOG = LogFactory.getLog(Tag.DB);
	private static final String[]	CINEMA_DETAILS		= { "_company", "_id", "name", "details_url", "territory",
														"address", "postcode", "telephone", "latitude", "longitude" };
	private static final String[]	GEOCACHE_DETAILS	= { "postcode", "latitude", "longitude" };
	private final DataBaseHelper	m_dataBaseHelper;

	DataBaseReader(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	public List<Cinema> getCinemas() {
		List<Cinema> cinemas = new ArrayList<Cinema>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Cinema", DataBaseReader.CINEMA_DETAILS, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Cinema cinema = getCinema(cursor);
			cinemas.add(cinema);
		}
		cursor.close();
		return cinemas;
	}

	private Cinema getCinema(final Cursor cursor) {
		int companyId = cursor.getInt(cursor.getColumnIndex("_company"));
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		String postcode = cursor.getString(cursor.getColumnIndex("postcode"));
		String detailsUrl = cursor.getString(cursor.getColumnIndex("details_url"));
		String territory = cursor.getString(cursor.getColumnIndex("territory"));
		String address = cursor.getString(cursor.getColumnIndex("address"));
		String telephone = cursor.getString(cursor.getColumnIndex("telephone"));
		int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
		int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));

		Cinema cinema = new Cinema();
		cinema.setSource(DataBaseReader.GENERIC_SOURCE);
		cinema.setCompanyId(companyId);
		cinema.setId(id);
		cinema.setName(name);
		cinema.setDetailsUrl(detailsUrl);
		cinema.setTerritory(territory);
		cinema.setAddress(address);
		cinema.setPostcode(postcode);
		cinema.setTelephone(telephone);
		cinema.setLocation(new GeoPoint(latitude, longitude));
		return cinema;
	}

	public int getNumberOfCinemas() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		int entries = (int) DatabaseUtils.queryNumEntries(db, "Cinema");
		return entries;
	}

	public Cinema getCinema(final int cinemaId) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Cinema", DataBaseReader.CINEMA_DETAILS, "_id = ?",
				new String[] { String.valueOf(cinemaId) }, null, null,
				null);
		Cinema cinema = null;
		if (cursor.moveToNext()) {
			cinema = getCinema(cursor);
		}
		cursor.close();
		return cinema;
	}

	public List<PostCodeLocation> getGeoCache() {
		List<PostCodeLocation> locations = new ArrayList<PostCodeLocation>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database
				.query("\"Helper:GeoCache\"", DataBaseReader.GEOCACHE_DETAILS, null, null, null, null, null);
		while (cursor.moveToNext()) {
			PostCodeLocation loc = getGeoCache(cursor);
			locations.add(loc);
		}
		cursor.close();
		return locations;
	}

	private PostCodeLocation getGeoCache(final Cursor cursor) {
		String postcode = cursor.getString(cursor.getColumnIndex("postcode"));
		double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
		double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

		GeoPoint geoLoc = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		PostCodeLocation loc = new PostCodeLocation(postcode, geoLoc);
		return loc;
	}
}
