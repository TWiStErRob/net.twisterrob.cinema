package com.twister.cineworld.model.json;

import java.io.IOException;
import java.net.*;
import java.util.*;

import android.util.Log;

import com.google.gson.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.response.*;
import com.twister.cineworld.ui.Tools;

public class CineworldAccessor {
	public static final String	DEVELOPER_KEY			= "9qfgpF7B";
	public static final String	BASE_URL_STRING			= "http://www.cineworld.co.uk/api/";
	public static final URL		BASE_URL;
	static {
		try {
			BASE_URL = new URL(CineworldAccessor.BASE_URL_STRING);
		} catch (MalformedURLException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	public static final URL		URL_CINEMAS_ALL			= CineworldAccessor.makeUrl("quickbook/cinemas", "full=true");
	public static final URL		URL_CINEMA_BY_ID		= CineworldAccessor.makeUrl("quickbook/cinemas", "full=true", "cinema=%d");
	public static final URL		URL_FILMS_ALL			= CineworldAccessor.makeUrl("quickbook/films", "full=true");
	public static final URL		URL_DATES_ALL			= CineworldAccessor.makeUrl("quickbook/dates");
	public static final URL		URL_PERFORMANCES		= CineworldAccessor.makeUrl("quickbook/performances", "cinema=%s", "film=%s", "date=%s");
	public static final URL		URL_CATEGORIES_ALL		= CineworldAccessor.makeUrl("categories");
	public static final URL		URL_EVENTS_ALL			= CineworldAccessor.makeUrl("events");
	public static final URL		URL_DISTRIBUTORS_ALL	= CineworldAccessor.makeUrl("distributors");

	private final Gson			m_gson;

	public CineworldAccessor() {
		m_gson = new GsonBuilder()
				.registerTypeAdapter(CineworldDate.class, new CineworldDateTypeConverter())
				.create();
	}

	public List<CineworldCinema> getAllCinemas() {
		return get(CineworldAccessor.URL_CINEMAS_ALL, "cinemas", CinemasResponse.class);
	}

	public List<CineworldFilm> getAllFilms() {
		return get(CineworldAccessor.URL_FILMS_ALL, "films", FilmsResponse.class);
	}

	public List<CineworldDate> getAllDates() {
		return get(CineworldAccessor.URL_DATES_ALL, "dates", DatesResponse.class);
	}

	public List<CineworldCategory> getAllCategories() {
		return get(CineworldAccessor.URL_CATEGORIES_ALL, "films", CategoriesResponse.class);
	}

	public List<CineworldEvent> getAllEvents() {
		return get(CineworldAccessor.URL_EVENTS_ALL, "events", EventsResponse.class);
	}

	public List<CineworldDistributor> getAllDistributors() {
		return get(CineworldAccessor.URL_DISTRIBUTORS_ALL, "distributors", DistributorsResponse.class);
	}

	public List<CineworldPerformance> getPeformances(final String cinema, final String film, final String date) {
		try {
			URL url = new URL(String.format(CineworldAccessor.URL_PERFORMANCES.toString(), cinema, film, date));
			return get(url, "performances", PerformancesResponse.class);
		} catch (MalformedURLException ex) {
			Log.e("ACCESS", String.format("Invalid URL getPerformances: cinema='%s', film='%s', date='%s'", cinema, film, date), ex);
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends CineworldBase, Response extends BaseResponse<? extends T>>
			List<T> get(final URL url, final String objectType, final Class<Response> clazz) {
		List<? extends T> result = Collections.emptyList();
		try {
			result = new JsonClient(m_gson).get(url, clazz).getList();
		} catch (IOException ex) {
			Log.d("ACCESS", "Unable to get all " + objectType, ex);
			Tools.toast(ex.getMessage()); // TODO
		} catch (URISyntaxException ex) {
			Log.d("ACCESS", "Unable to get all " + objectType, ex);
			Tools.toast(ex.getMessage()); // TODO
		}
		return (List<T>) result; // TODO review generic bounds
	}

	private static URL makeUrl(final String requestType, final String... filters) {
		StringBuilder filterString = new StringBuilder();
		for (String filter : filters) {
			filterString.append("&" + filter);
		}

		try {
			String spec = String.format("%s?key=%s%s", requestType, CineworldAccessor.DEVELOPER_KEY, filterString);
			return new URL(CineworldAccessor.BASE_URL, spec);
		} catch (MalformedURLException ex) {
			Log.e("ACCESS", String.format("Cannot initialize urls: %s (%s)", requestType, Arrays.toString(filters), ex));
			return CineworldAccessor.BASE_URL;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// More specific methods
	// ////////////////////////////////////////////////////////////////////////////////////

	public CineworldCinema getCinema(final long cinemaId) {
		try {
			URL url = new URL(String.format(CineworldAccessor.URL_CINEMA_BY_ID.toString(), cinemaId));
			List<CineworldCinema> cinemas = get(url, "cinema", CinemasResponse.class);
			if (cinemas.isEmpty()) {
				return null;
			} else if (cinemas.size() == 1) {
				return cinemas.get(0);
			} else {
				Log.w("ACCESS", String.format("Multiple cinemas returned for id=%d", cinemaId));
				return null;
			}
		} catch (MalformedURLException ex) {
			Log.e("ACCESS", String.format("Invalid URL getCinema: cinemaId='%d', film='%s', date='%s'", cinemaId), ex);
			return null;
		}
	}
}
