package com.twister.cineworld.model.json;

import java.net.URL;
import java.util.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.response.*;

public class MockClient implements JsonClient {
	private static final Map<Class<? extends BaseListResponse<?>>, BaseListResponse<?>>	RESPONSE_MAPPING	= new HashMap<Class<? extends BaseListResponse<?>>, BaseListResponse<?>>();
	static {
		MockClient.RESPONSE_MAPPING.put(CinemasResponse.class, MockClient.createCinemasResponse());
		MockClient.RESPONSE_MAPPING.put(FilmsResponse.class, MockClient.createFilmsResponse());
		MockClient.RESPONSE_MAPPING.put(DatesResponse.class, MockClient.createDatesResponse());
		MockClient.RESPONSE_MAPPING.put(PerformancesResponse.class, MockClient.createPerformancesResponse());
		MockClient.RESPONSE_MAPPING.put(CategoriesResponse.class, MockClient.createCategoriesResponse());
		MockClient.RESPONSE_MAPPING.put(EventsResponse.class, MockClient.createEventsResponse());
		MockClient.RESPONSE_MAPPING.put(DistributorsResponse.class, MockClient.createDistributorsResponse());
	}

	public <T> T get(final URL url, final Class<T> responseType) throws ApplicationException {
		return any(responseType);
	}

	public <T> T post(final String url, final Object requestObject, final Class<T> responseType)
			throws ApplicationException {
		return any(responseType);
	}

	/**
	 * Magic happens here.
	 */
	protected <T> T any(final Class<T> responseType) throws ApplicationException {
		@SuppressWarnings("unchecked")
		T response = (T) MockClient.RESPONSE_MAPPING.get(responseType);
		return response;
	}

	private static CinemasResponse createCinemasResponse() {
		CinemasResponse cinemasResponse = new CinemasResponse();
		{
			List<CineworldCinema> cinemas = new LinkedList<CineworldCinema>();
			{
				CineworldCinema cinema = new CineworldCinema();
				cinema.setName("Cinema 1");
				cinema.setPostcode("W6 9JT");
				cinemas.add(cinema);
			}
			{
				CineworldCinema cinema = new CineworldCinema();
				cinema.setName("Cinema 2");
				cinemas.add(cinema);
			}
			{
				CineworldCinema cinema = new CineworldCinema();
				cinema.setName("Cinema 3");
				cinema.setPostcode("W1D 7DH");
				cinemas.add(cinema);
			}
			cinemasResponse.setCinemas(cinemas);
		}
		return cinemasResponse;
	}

	private static FilmsResponse createFilmsResponse() {
		FilmsResponse filmsResponse = new FilmsResponse();
		{
			List<CineworldFilm> films = new LinkedList<CineworldFilm>();
			{
				CineworldFilm film = new CineworldFilm();
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("Regular 2D movie");
				film.setPosterUrl("http://www.cineworld.co.uk/assets/media/films/5438_poster.jpg");
				film.setClassification("none");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("2D - Every type movie");
				film.set3D(false);
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("3D - Every type movie");
				film.set3D(true);
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("IMAX - Every type movie");
				film.setIMax(true);
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("IMAX 3D - Every type movie");
				film.setIMax(true);
				film.set3D(true);
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("Multipart movie: Part 1");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("Multipart movie: Part 2");
				film.setPosterUrl("http://www.cineworld.co.uk/assets/media/films/5437_poster.jpg");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("Multipart movie: Part 3");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("2D - Multipart 3D movie: Part 1");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("3D - Multipart 3D movie: Part 1");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("2D - Multipart 3D movie: Part 2");
				films.add(film);
			}
			{
				CineworldFilm film = new CineworldFilm();
				film.setTitle("3D - Multipart 3D movie: Part 2");
				films.add(film);
			}
			filmsResponse.setFilms(films);
		}
		return filmsResponse;
	}

	private static DatesResponse createDatesResponse() {
		DatesResponse datesResponse = new DatesResponse();
		{
			List<CineworldDate> dates = new LinkedList<CineworldDate>();
			{
				CineworldDate date = new CineworldDate();
				dates.add(date);
			}
			{
				CineworldDate date = new CineworldDate();
				date.setDate("19860701");
				dates.add(date);
			}
			datesResponse.setDates(dates);
		}
		return datesResponse;
	}

	private static PerformancesResponse createPerformancesResponse() {
		PerformancesResponse performancesResponse = new PerformancesResponse();
		{
			List<CineworldPerformance> performances = new LinkedList<CineworldPerformance>();
			{
				CineworldPerformance performance = new CineworldPerformance();
				performances.add(performance);
			}
			performancesResponse.setPerformances(performances);
		}
		return performancesResponse;
	}

	private static CategoriesResponse createCategoriesResponse() {
		CategoriesResponse categoriesResponse = new CategoriesResponse();
		{
			List<CineworldCategory> categories = new LinkedList<CineworldCategory>();
			{
				CineworldCategory category = new CineworldCategory();
				categories.add(category);
			}
			categoriesResponse.setCategories(categories);
		}
		return categoriesResponse;
	}

	private static EventsResponse createEventsResponse() {
		EventsResponse eventsResponse = new EventsResponse();
		{
			List<CineworldEvent> events = new LinkedList<CineworldEvent>();
			{
				CineworldEvent event = new CineworldEvent();
				events.add(event);
			}
			eventsResponse.setEvents(events);
		}
		return eventsResponse;
	}

	private static DistributorsResponse createDistributorsResponse() {
		DistributorsResponse distributorsResponse = new DistributorsResponse();
		{
			List<CineworldDistributor> distributors = new LinkedList<CineworldDistributor>();
			{
				CineworldDistributor distributor = new CineworldDistributor();
				distributors.add(distributor);
			}
			distributorsResponse.setDistributors(distributors);
		}
		return distributorsResponse;
	}
}
