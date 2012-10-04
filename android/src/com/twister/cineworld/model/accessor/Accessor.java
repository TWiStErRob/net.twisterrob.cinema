package com.twister.cineworld.model.accessor;

import java.util.List;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.json.TimeSpan;

public interface Accessor {

	List<Cinema> getAllCinemas() throws ApplicationException;

	Cinema getCinema(int cinemaId) throws ApplicationException;

	List<Cinema> getCinemas(int filmEdi) throws ApplicationException;

	List<Film> getAllFilms() throws ApplicationException;

	Film getFilm(int filmEdi) throws ApplicationException;

	List<Film> getFilms(int cinemaId) throws ApplicationException;

	List<Film> getFilms(int cinemaId, TimeSpan span) throws ApplicationException;

	List<Date> getAllDates() throws ApplicationException;

	List<Date> getDates(int filmEdi) throws ApplicationException;

	List<Category> getAllCategories() throws ApplicationException;

	List<Event> getAllEvents() throws ApplicationException;

	List<Distributor> getAllDistributors() throws ApplicationException;

	List<Performance> getPeformances(int cinemaId, int filmEdi, int date) throws ApplicationException;

}
