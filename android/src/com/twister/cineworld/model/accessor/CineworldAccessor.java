package com.twister.cineworld.model.accessor;

import java.util.List;

import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.json.TimeSpan;
import com.twister.cineworld.model.json.data.*;

public interface CineworldAccessor {

	List<Cinema> getAllCinemas() throws CineworldException;

	CineworldCinema getCinema(int cinemaId) throws CineworldException;

	List<CineworldCinema> getCinemas(int filmEdi) throws CineworldException;

	List<CineworldFilm> getAllFilms() throws CineworldException;

	CineworldFilm getFilm(int filmEdi) throws CineworldException;

	List<CineworldFilm> getFilms(int cinemaId) throws CineworldException;

	List<CineworldFilm> getFilms(int cinemaId, TimeSpan span) throws CineworldException;

	List<CineworldDate> getAllDates() throws CineworldException;

	List<CineworldDate> getDates(int filmEdi) throws CineworldException;

	List<Category> getAllCategories() throws CineworldException;

	List<Event> getAllEvents() throws CineworldException;

	List<Distributor> getAllDistributors() throws CineworldException;

	List<Performance> getPeformances(int cinemaId, int filmEdi, int date) throws CineworldException;

}
