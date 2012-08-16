package com.twister.cineworld.model.json;

import java.io.IOException;
import java.net.*;
import java.util.*;

import android.util.Log;

import com.google.gson.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.request.*;
import com.twister.cineworld.model.json.response.*;
import com.twister.cineworld.ui.Tools;

public class CineworldAccessor {
	public static final URL	URL_PERFORMANCES	 = BaseListRequest.makeUrl("quickbook/performances", "cinema=%s",
	                                                     "film=%s", "date=%s");
	public static final URL	URL_CATEGORIES_ALL	 = BaseListRequest.makeUrl("categories");
	public static final URL	URL_EVENTS_ALL	     = BaseListRequest.makeUrl("events");
	public static final URL	URL_DISTRIBUTORS_ALL	= BaseListRequest.makeUrl("distributors");

	private final Gson	    m_gson;

	public CineworldAccessor() {
		m_gson = new GsonBuilder()
		        .registerTypeAdapter(CineworldDate.class, new CineworldDateTypeConverter())
		        .create();
	}

	public List<CineworldCinema> getAllCinemas() {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		return getList(request);
	}

	public CineworldCinema getCinema(final int cinemaId) {
		CinemasRequest request = new CinemasRequest();
		request.setCinema(cinemaId);
		request.setFull(true);
		return getSingular(request, cinemaId);
	}

	public List<CineworldCinema> getCinemas(final int filmEdi) {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		request.setFilm(filmEdi);
		return getList(request);
	}

	public List<CineworldFilm> getAllFilms() {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		return getList(request);
	}

	public CineworldFilm getFilm(final int filmEdi) {
		FilmsRequest request = new FilmsRequest();
		request.setFilm(filmEdi);
		request.setFull(true);
		return getSingular(request, filmEdi);
	}

	public List<CineworldFilm> getFilms(final int cinemaId) {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		request.setCinema(cinemaId);
		return getList(request);
	}

	public List<CineworldDate> getAllDates() {
		DatesRequest request = new DatesRequest();
		return getList(request);
	}

	public List<CineworldDate> getDates(final int filmEdi) {
		DatesRequest request = new DatesRequest();
		request.setFilm(filmEdi);
		return getList(request);
	}

	public List<CineworldCategory> getAllCategories() {
		return get(CineworldAccessor.URL_CATEGORIES_ALL, "categories", CategoriesResponse.class);
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
			Log.e("ACCESS",
			        String.format("Invalid URL getPerformances: cinema='%s', film='%s', date='%s'", cinema, film, date),
			        ex);
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends CineworldBase, Response extends BaseListResponse<? extends T>>
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

	private <T extends CineworldBase> List<T> getList(final BaseListRequest<T> request) {
		return get(request.getURL(), request.getRequestType(), request.getResponseClass());
	}

	private <T extends CineworldBase> T getSingular(final BaseListRequest<T> request, final Object parameter) {
		List<T> list = getList(request);
		if (list.isEmpty()) {
			return null; // TODO
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			Log.w("ACCESS", String.format("Multiple %s returned for parameter=%s", request.getRequestType(), parameter));
			return null; // TODO
		}
	}
}
