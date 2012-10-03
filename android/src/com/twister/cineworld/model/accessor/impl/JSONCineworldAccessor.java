package com.twister.cineworld.model.accessor.impl;

import java.util.*;

import com.google.gson.*;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.model.accessor.CineworldAccessor;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.json.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.request.*;
import com.twister.cineworld.model.json.response.BaseListResponse;

public class JSONCineworldAccessor implements CineworldAccessor {

	private final JsonClient	m_jsonClient;

	public JSONCineworldAccessor() {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(CineworldDate.class, new CineworldDateTypeConverter())
				.create();
		m_jsonClient = new JsonClient(gson);
	}

	public List<Cinema> getAllCinemas() throws CineworldException {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		List<CineworldCinema> responseCinemas = getList(request);
		List<Cinema> cinemas = convert(responseCinemas);

		return cinemas;
	}

	public CineworldCinema getCinema(final int cinemaId) throws CineworldException {
		CinemasRequest request = new CinemasRequest();
		request.setCinema(cinemaId);
		request.setFull(true);
		return getSingular(request, cinemaId);
	}

	public List<CineworldCinema> getCinemas(final int filmEdi) throws CineworldException {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		request.setFilm(filmEdi);
		return getList(request);
	}

	public List<CineworldFilm> getAllFilms() throws CineworldException {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		return getList(request);
	}

	public CineworldFilm getFilm(final int filmEdi) throws CineworldException {
		FilmsRequest request = new FilmsRequest();
		request.setFilm(filmEdi);
		request.setFull(true);
		return getSingular(request, filmEdi);
	}

	public List<CineworldFilm> getFilms(final int cinemaId) throws CineworldException {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		request.setCinema(cinemaId);
		return getList(request);
	}

	public List<CineworldFilm> getFilms(final int cinemaId, final TimeSpan span) throws CineworldException {
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

	public List<CineworldDate> getAllDates() throws CineworldException {
		DatesRequest request = new DatesRequest();
		return getList(request);
	}

	public List<CineworldDate> getDates(final int filmEdi) throws CineworldException {
		DatesRequest request = new DatesRequest();
		request.setFilm(filmEdi);
		return getList(request);
	}

	public List<Category> getAllCategories() throws CineworldException {
		CategoriesRequest request = new CategoriesRequest();
		List<CineworldCategory> list = getList(request);
		List<Category> result = convert(list);
		return result;
	}

	public List<Event> getAllEvents() throws CineworldException {
		EventsRequest request = new EventsRequest();
		List<CineworldEvent> list = getList(request);
		List<Event> result = convert(list);
		return result;
	}

	public List<Distributor> getAllDistributors() throws CineworldException {
		DistributorsRequest request = new DistributorsRequest();
		List<CineworldDistributor> list = getList(request);
		List<Distributor> result = convert(list);
		return result;
	}

	public List<Performance> getPeformances(final int cinemaId, final int filmEdi, final int date)
			throws CineworldException {
		PerformancesRequest request = new PerformancesRequest();
		request.setCinema(cinemaId);
		request.setFilm(filmEdi);
		request.setDate(date);
		List<CineworldPerformance> list = getList(request);
		List<Performance> result = convert(list);
		return result;
	}

	private <T extends CineworldBase> List<T> getList(final BaseListRequest<T> request) throws CineworldException {
		BaseListResponse<T> response = this.m_jsonClient.get(request.getURL(), request.getResponseClass());
		if (response.getErrors() != null && !response.getErrors().isEmpty()) {
			throw new InternalException("Errors in JSON response: " + response.getErrors());
		}
		return response.getList();
	}

	private <T extends CineworldBase> T getSingular(final BaseListRequest<T> request, final Object parameter)
			throws CineworldException {
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

	private <TOut extends GenericBase, TIn extends CineworldBase> List<TOut> convert(final List<TIn> responseItems) {
		List<TOut> cinemas = new ArrayList<TOut>(responseItems.size());
		for (TIn cineworldObject : responseItems) {
			TOut object = convert(cineworldObject);
			cinemas.add(object);
		}
		return cinemas;
	}

	// TODO Dozer
	@SuppressWarnings("unchecked")
	private <TOut extends GenericBase, TIn extends CineworldBase> TOut convert(final TIn cineworldObject) {
		GenericBase result = null;
		if (cineworldObject instanceof CineworldCinema) {
			CineworldCinema cineworld = (CineworldCinema) cineworldObject;
			Cinema generic = new Cinema();
			generic.setId(cineworld.getId());
			generic.setName(cineworld.getName());
			generic.setUrl(cineworld.getCinemaUrl());
			generic.setTelephone(cineworld.getTelephone());
			generic.setAddress(cineworld.getAddress());
			generic.setPostcode(cineworld.getPostcode());
			result = generic;
		} else if (cineworldObject instanceof CineworldCategory) {
			CineworldCategory cineworld = (CineworldCategory) cineworldObject;
			Category generic = new Category();
			generic.setCode(cineworld.getCode());
			generic.setName(cineworld.getName());
			result = generic;
		} else if (cineworldObject instanceof CineworldEvent) {
			CineworldEvent cineworld = (CineworldEvent) cineworldObject;
			Event generic = new Event();
			generic.setCode(cineworld.getCode());
			generic.setName(cineworld.getName());
			result = generic;
		} else if (cineworldObject instanceof CineworldDistributor) {
			CineworldDistributor cineworld = (CineworldDistributor) cineworldObject;
			Distributor generic = new Distributor();
			generic.setId(cineworld.getId());
			generic.setName(cineworld.getName());
			result = generic;
		} else if (cineworldObject instanceof CineworldPerformance) {
			CineworldPerformance cineworld = (CineworldPerformance) cineworldObject;
			Performance generic = new Performance();
			generic.setTime(cineworld.getTime());
			generic.setType(cineworld.getType());
			generic.setBookingUrl(cineworld.getBookingUrl());
			generic.setAvailable(cineworld.isAvailable());
			generic.setSubtitled(cineworld.isSubtitled());
			generic.setAudioDescribed(cineworld.isAudioDescribed());
			result = generic;
		}
		return (TOut) result;
	}
}
