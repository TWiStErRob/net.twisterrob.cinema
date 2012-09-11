package com.twister.cineworld.model.json;

import java.util.*;

import com.google.gson.*;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.request.*;
import com.twister.cineworld.model.json.response.BaseListResponse;

public class CineworldAccessor {

	private final JsonClient	m_jsonClient;

	public CineworldAccessor() {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(CineworldDate.class, new CineworldDateTypeConverter())
				.create();
		m_jsonClient = new JsonClient(gson);
	}

	public List<CineworldCinema> getAllCinemas() throws InternalException, ExternalException, NetworkException {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		return getList(request);
	}

	public CineworldCinema getCinema(final int cinemaId) throws InternalException, ExternalException, NetworkException {
		CinemasRequest request = new CinemasRequest();
		request.setCinema(cinemaId);
		request.setFull(true);
		return getSingular(request, cinemaId);
	}

	public List<CineworldCinema> getCinemas(final int filmEdi) throws InternalException, ExternalException,
			NetworkException {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		request.setFilm(filmEdi);
		return getList(request);
	}

	public List<CineworldFilm> getAllFilms() throws InternalException, ExternalException, NetworkException {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		return getList(request);
	}

	public CineworldFilm getFilm(final int filmEdi) throws InternalException, ExternalException, NetworkException {
		FilmsRequest request = new FilmsRequest();
		request.setFilm(filmEdi);
		request.setFull(true);
		return getSingular(request, filmEdi);
	}

	public List<CineworldFilm> getFilms(final int cinemaId) throws InternalException, ExternalException,
			NetworkException {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		request.setCinema(cinemaId);
		return getList(request);
	}

	public List<CineworldFilm> getFilms(final int cinemaId, final TimeSpan span) throws InternalException,
			ExternalException, NetworkException {
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

	public List<CineworldDate> getAllDates() throws InternalException, ExternalException, NetworkException {
		DatesRequest request = new DatesRequest();
		return getList(request);
	}

	public List<CineworldDate> getDates(final int filmEdi) throws InternalException, ExternalException,
			NetworkException {
		DatesRequest request = new DatesRequest();
		request.setFilm(filmEdi);
		return getList(request);
	}

	public List<CineworldCategory> getAllCategories() throws InternalException, ExternalException, NetworkException {
		CategoriesRequest request = new CategoriesRequest();
		return getList(request);
	}

	public List<CineworldEvent> getAllEvents() throws InternalException, ExternalException, NetworkException {
		EventsRequest request = new EventsRequest();
		return getList(request);
	}

	public List<CineworldDistributor> getAllDistributors() throws InternalException, ExternalException,
			NetworkException {
		DistributorsRequest request = new DistributorsRequest();
		return getList(request);
	}

	public List<CineworldPerformance> getPeformances(final int cinemaId, final int filmEdi, final int date)
			throws InternalException, ExternalException, NetworkException {
		PerformancesRequest request = new PerformancesRequest();
		request.setCinema(cinemaId);
		request.setFilm(filmEdi);
		request.setDate(date);
		return getList(request);
	}

	private <T extends CineworldBase> List<T> getList(final BaseListRequest<T> request) throws InternalException,
			ExternalException, NetworkException {
		BaseListResponse<T> response = this.m_jsonClient.get(request.getURL(), request.getResponseClass());
		if (response.getErrors() != null && !response.getErrors().isEmpty()) {
			throw new InternalException("Errors in JSON response: " + response.getErrors());
		}
		return response.getList();
	}

	private <T extends CineworldBase> T getSingular(final BaseListRequest<T> request, final Object parameter)
			throws InternalException, ExternalException, NetworkException {
		List<T> list = getList(request);
		if (list.isEmpty()) {
			throw new InternalException("No results for request");
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new InternalException(String.format(
					"Multiple %s returned for parameter=%s", request.getRequestType(), parameter));
		}
	}
}
