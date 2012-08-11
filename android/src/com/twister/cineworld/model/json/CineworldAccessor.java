package com.twister.cineworld.model.json;

import java.io.IOException;
import java.util.*;

import android.util.Log;

import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.response.*;
import com.twister.cineworld.ui.Tools;

public class CineworldAccessor {
	public static final String	DEVELOPER_KEY			= "9qfgpF7B";
	public static final String	BASE_URI				= "http://www.cineworld.co.uk/api";
	public static final String	URI_FILMS_ALL			= CineworldAccessor.makeUri("quickbook/films", true);
	public static final String	URI_CINEMAS_ALL			= CineworldAccessor.makeUri("quickbook/cinemas", true);
	public static final String	URI_DATES_ALL			= CineworldAccessor.makeUri("quickbook/dates", true);
	public static final String	URI_PERFORMANCES_ALL	= CineworldAccessor.makeUri("quickbook/performances", true);
	public static final String	URI_CATEGORIES_ALL		= CineworldAccessor.makeUri("categories", true);
	public static final String	URI_EVENTS_ALL			= CineworldAccessor.makeUri("events", true);
	public static final String	URI_DISTRIBUTORS_ALL	= CineworldAccessor.makeUri("distributors", true);

	public List<CineworldFilm> getAllFilms() {
		return getAll(CineworldAccessor.URI_FILMS_ALL, "films", FilmsResponse.class);
	}

	public List<CineworldCinema> getAllCinemas() {
		return getAll(CineworldAccessor.URI_CINEMAS_ALL, "cinemas", CinemasResponse.class);
	}

	public List<CineworldDate> getAllDates() {
		return getAll(CineworldAccessor.URI_DATES_ALL, "dates", DatesResponse.class);
	}

	public List<CineworldCategory> getAllCategories() {
		return getAll(CineworldAccessor.URI_CATEGORIES_ALL, "films", CategoriesResponse.class);
	}

	public List<CineworldEvent> getAllEvents() {
		return getAll(CineworldAccessor.URI_EVENTS_ALL, "events", EventsResponse.class);
	}

	public List<CineworldDistributor> getAllDistributors() {
		return getAll(CineworldAccessor.URI_DISTRIBUTORS_ALL, "distributors", DistributorsResponse.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends CineworldBase, Response extends BaseResponse<? extends T>>
			List<T> getAll(final String url, final String objectType, final Class<Response> clazz) {
		List<? extends T> result = Collections.emptyList();
		try {
			result = new JsonClient().get(url, clazz).getList();
		} catch (IOException ex) {
			Log.d("ACCESS", "Unable to get all " + objectType, ex);
			Tools.toast(ex.getMessage()); // TODO
		}
		return (List<T>) result; // TODO review generic bounds
	}

	private static String makeUri(final String requestType, final boolean full, final String... filters) {
		StringBuilder filterString = new StringBuilder();
		for (String filter : filters) {
			filterString.append("&" + filter);
		}
		return String.format("%s/%s?key=%s&full=%s&%s", CineworldAccessor.BASE_URI, requestType, CineworldAccessor.DEVELOPER_KEY, full, filterString);
	}
}
