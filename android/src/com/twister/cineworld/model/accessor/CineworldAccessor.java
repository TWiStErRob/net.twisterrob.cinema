package com.twister.cineworld.model.accessor;

import java.util.List;

import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.generic.Cinema;
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

	List<CineworldCategory> getAllCategories() throws CineworldException;

	List<CineworldEvent> getAllEvents() throws CineworldException;

	List<CineworldDistributor> getAllDistributors() throws CineworldException;

	List<CineworldPerformance> getPeformances(int cinemaId, int filmEdi, int date) throws CineworldException;

}
