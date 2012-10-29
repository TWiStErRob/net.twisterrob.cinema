package com.twister.cineworld.model;

import java.util.*;

import com.twister.cineworld.model.generic.*;

public class MovieMatcher {
	public List<Movie> match(final List<Film> cineFilms) {
		List<Movie> films = new LinkedList<Movie>();
		for (Film cineFilm : cineFilms) {
			String title = cineFilm.getTitle();
			if (title != null) {
				String[] parts = title.split("\\s*-\\s*", 2);
				// Log.d("MATCH", Arrays.toString(parts));
				if (parts.length == 2 && parts[1].matches("\\d{2}/\\d{2}/\\d{4}")) {
					title = parts[0];
				} else if (parts.length == 1) {
					title = parts[1];
				} else {
					title = parts[0];
				}
			}
			Movie film = find(films, title);
			if (film == null) {
				film = new Movie();
				film.setTitle(title);
				film.setClassification(cineFilm.getClassification());
				film.setAdvisory(cineFilm.getAdvisory());
			}
			film.add(cineFilm);
			if (find(films, film.getTitle()) == null) {
				films.add(film);
			}
		}
		return films;
	}

	public List<MovieBase> matchSeries(final List<Movie> films) {
		List<MovieSerie> filmSeries = new LinkedList<MovieSerie>();
		for (Movie film : films) {
			String title = film.getTitle();
			if (title != null) {
				String[] parts = title.split("\\s*:\\s*", 2);
				// Log.d("MATCH", Arrays.toString(parts));
				title = parts[0]; // either the whole or the basic
			}
			MovieSerie filmSerie = findSerie(filmSeries, title);
			if (filmSerie == null) {
				filmSerie = new MovieSerie();
				filmSerie.setTitle(title);
				filmSerie.setClassification(film.getClassification());
				filmSerie.setAdvisory(film.getAdvisory());
			}
			filmSerie.add(film);
			if (findSerie(filmSeries, filmSerie.getTitle()) == null) {
				filmSeries.add(filmSerie);
			}
		}
		List<MovieBase> filmList = new ArrayList<MovieBase>(filmSeries.size());
		for (MovieSerie filmSerie : filmSeries) {
			filmList.add(filmSerie.toFilm());
		}
		return filmList;
	}

	private MovieSerie findSerie(final List<MovieSerie> filmSeries, final String string) {
		if (string != null) {
			for (MovieSerie filmSerie : filmSeries) {
				if (string.equals(filmSerie.getTitle())) {
					return filmSerie;
				}
			}
		}
		return null;
	}

	private Movie find(final List<Movie> films, final String string) {
		if (string != null) {
			for (Movie film : films) {
				if (string.equals(film.getTitle())) {
					return film;
				}
			}
		}
		return null;
	}
}
