package com.twister.cineworld.model.accessor;

import java.util.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.generic.Date;
import com.twister.cineworld.model.json.TimeSpan;

public interface Accessor {
	List<Cinema> getAllCinemas() throws ApplicationException;

	Cinema getCinema(int cinemaId) throws ApplicationException;

	List<Cinema> getCinemasForFilm(int filmEdi) throws ApplicationException;

	List<Cinema> getCinemasForDistributor(int distributorId) throws ApplicationException;

	List<Cinema> getCinemasForEvent(String eventCode) throws ApplicationException;

	List<Cinema> getCinemasForCategory(String categoryCode) throws ApplicationException;

	List<Cinema> getCinemasForDate(Calendar calendar) throws ApplicationException;

	List<Film> getAllFilms() throws ApplicationException;

	Film getFilm(int filmEdi) throws ApplicationException;

	List<Film> getFilmsForCinema(int cinemaId) throws ApplicationException;

	List<Film> getFilmsForCinema(int cinemaId, TimeSpan span) throws ApplicationException;

	List<Film> getFilmsForDistributor(int distributorId) throws ApplicationException;

	List<Film> getFilmsForEvent(String eventCode) throws ApplicationException;

	List<Film> getFilmsForCategory(String categoryCode) throws ApplicationException;

	List<Film> getFilmsForDate(Calendar calendar) throws ApplicationException;

	List<Date> getAllDates() throws ApplicationException;

	List<Date> getDatesForFilm(int filmEdi) throws ApplicationException;

	List<Date> getDatesForCinema(int cinemaId) throws ApplicationException;

	List<Date> getDatesForFilmAtCinema(int filmEdi, int cinemaId) throws ApplicationException;

	List<Category> getAllCategories() throws ApplicationException;

	List<Event> getAllEvents() throws ApplicationException;

	List<Distributor> getAllDistributors() throws ApplicationException;

	List<Performance> getPeformances(int cinemaId, int filmEdi, String date) throws ApplicationException;
}
