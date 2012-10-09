package com.twister.cineworld.db;

import java.util.*;

import android.database.*;
import android.database.sqlite.SQLiteDatabase;

import com.twister.cineworld.model.generic.*;

class DataBaseReader {
	private static final String		GENERIC_SOURCE		= "DB";
	// private static final CineworldLogger LOG = LogFactory.getLog(Tag.DB);
	private static final String[]	CINEMA_DETAILS		= { "_company", "_id", "name", "details_url", "territory",
														"address", "postcode", "telephone", "latitude", "longitude" };
	private static final String[]	CATEGORY_DETAILS	= { "code", "name" };
	private static final String[]	EVENT_DETAILS		= { "code", "name" };
	private static final String[]	DISTRIBUTOR_DETAILS	= { "_id", "name" };
	private static final String[]	GEOCACHE_DETAILS	= { "postcode", "latitude", "longitude" };
	private final DataBaseHelper	m_dataBaseHelper;

	DataBaseReader(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	// #region Model::Cinemas

	public List<Cinema> getCinemas() {
		List<Cinema> cinemas = new ArrayList<Cinema>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Cinema", CINEMA_DETAILS, null, null, null, null, null);
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
		double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
		double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

		Cinema cinema = new Cinema();
		cinema.setSource(GENERIC_SOURCE);
		cinema.setCompanyId(companyId);
		cinema.setId(id);
		cinema.setName(name);
		cinema.setDetailsUrl(detailsUrl);
		cinema.setTerritory(territory);
		cinema.setAddress(address);
		cinema.setPostcode(postcode);
		cinema.setTelephone(telephone);
		cinema.setLocation(new Location(latitude, longitude));
		return cinema;
	}

	public int getNumberOfCinemas() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		int entries = (int) DatabaseUtils.queryNumEntries(db, "Cinema");
		return entries;
	}

	public Cinema getCinema(final int cinemaId) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Cinema", CINEMA_DETAILS, "_id = ?", new String[] { String.valueOf(cinemaId) },
				null, null, null);
		Cinema cinema = null;
		if (cursor.moveToNext()) {
			cinema = getCinema(cursor);
		}
		cursor.close();
		return cinema;
	}

	// #endregion

	// #region Model::Satellite (Category, Event, Distributor)

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("FilmCategory", CATEGORY_DETAILS, null, null, null, null, "name");
		while (cursor.moveToNext()) {
			Category category = getCategory(cursor);
			categories.add(category);
		}
		cursor.close();
		return categories;
	}

	private Category getCategory(final Cursor cursor) {
		String code = cursor.getString(cursor.getColumnIndex("code"));
		String name = cursor.getString(cursor.getColumnIndex("name"));

		Category category = new Category();
		category.setSource(GENERIC_SOURCE);
		category.setCode(code);
		category.setName(name);
		return category;
	}

	public List<Event> getEvents() {
		List<Event> events = new ArrayList<Event>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Event", EVENT_DETAILS, null, null, null, null, "name");
		while (cursor.moveToNext()) {
			Event event = getEvent(cursor);
			events.add(event);
		}
		cursor.close();
		return events;
	}

	private Event getEvent(final Cursor cursor) {
		String code = cursor.getString(cursor.getColumnIndex("code"));
		String name = cursor.getString(cursor.getColumnIndex("name"));

		Event event = new Event();
		event.setSource(GENERIC_SOURCE);
		event.setCode(code);
		event.setName(name);
		return event;
	}

	public List<Distributor> getDistributors() {
		List<Distributor> distributors = new ArrayList<Distributor>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("FilmDistributor", DISTRIBUTOR_DETAILS, null, null, null, null, "name");
		while (cursor.moveToNext()) {
			Distributor distributor = getDistributor(cursor);
			distributors.add(distributor);
		}
		cursor.close();
		return distributors;
	}

	private Distributor getDistributor(final Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		String name = cursor.getString(cursor.getColumnIndex("name"));

		Distributor distributor = new Distributor();
		distributor.setSource(GENERIC_SOURCE);
		distributor.setId(id);
		distributor.setName(name);
		return distributor;
	}

	// #endregion

	// #region internal

	public List<PostCodeLocation> getGeoCache() {
		List<PostCodeLocation> locations = new ArrayList<PostCodeLocation>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("\"Helper:GeoCache\"", GEOCACHE_DETAILS, null, null, null, null, null);
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

		Location geoLoc = new Location(latitude, longitude);

		PostCodeLocation postLoc = new PostCodeLocation(postcode, geoLoc);
		return postLoc;
	}

	// #endregion
}
