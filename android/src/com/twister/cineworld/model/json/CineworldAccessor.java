package com.twister.cineworld.model.json;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import com.google.gson.*;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.request.*;
import com.twister.cineworld.ui.Tools;

public class CineworldAccessor {

	private static final CineworldLogger	LOG	= LogFactory.getLog(Tag.ACCESS);

	private final Gson						m_gson;

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

	public List<CineworldFilm> getFilms(final int cinemaId, final TimeSpan span) {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		request.setCinema(cinemaId);
		if (span == TimeSpan.Tomorrow) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, 1);
			request.setDate(now);
		}
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
		CategoriesRequest request = new CategoriesRequest();
		return getList(request);
	}

	public List<CineworldEvent> getAllEvents() {
		EventsRequest request = new EventsRequest();
		return getList(request);
	}

	public List<CineworldDistributor> getAllDistributors() {
		DistributorsRequest request = new DistributorsRequest();
		return getList(request);
	}

	public List<CineworldPerformance> getPeformances(final int cinemaId, final int filmEdi, final int date) {
		PerformancesRequest request = new PerformancesRequest();
		request.setCinema(cinemaId);
		request.setFilm(filmEdi);
		request.setDate(date);
		return getList(request);
	}

	@SuppressWarnings("unchecked")
	private <T extends CineworldBase> List<T> getList(final BaseListRequest<T> request) {
		String objectType = request.getRequestType();
		List<? extends T> result = Collections.emptyList();
		try {
			result = new JsonClient(m_gson).get(request.getURL(), request.getResponseClass()).getList();
		} catch (IOException ex) {
			if (CineworldAccessor.LOG.isDebugEnabled()) {
				CineworldAccessor.LOG.debug("Unable to get all " + objectType, ex);
			}
			Tools.toast(ex.getMessage()); // TODO
		} catch (URISyntaxException ex) {
			if (CineworldAccessor.LOG.isDebugEnabled()) {
				CineworldAccessor.LOG.debug("Unable to get all " + objectType, ex);
			}
			Tools.toast(ex.getMessage()); // TODO
		}
		return (List<T>) result; // TODO review generic bounds
	}

	private <T extends CineworldBase> T getSingular(final BaseListRequest<T> request, final Object parameter) {
		List<T> list = getList(request);
		if (list.isEmpty()) {
			return null; // TODO
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			CineworldAccessor.LOG.warn(String.format(
					"Multiple %s returned for parameter=%s", request.getRequestType(), parameter));
			return null; // TODO
		}
	}
}
