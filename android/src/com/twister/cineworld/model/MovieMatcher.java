package com.twister.cineworld.model;

import java.util.*;
import java.util.regex.*;

import com.twister.cineworld.model.generic.*;

public class MovieMatcher {
	public List<Movie> match(final List<Film> cineFilms) {
		List<Movie> films = new LinkedList<Movie>();
		Pattern partsRegex = Pattern.compile("^(.*)\\s*-\\s*(\\d{2}/\\d{2}/\\d{4})$");
		Pattern qualifierRegex = Pattern.compile("^(?:\\((2D|3D|IMAX ?3-?D)\\)|M4J|SubM4J) (.*)$");
		for (Film cineFilm : cineFilms) {
			String title = cineFilm.getTitle();
			if (title != null) {
				// strip date from the end (historical, don't exactly know why)
				Matcher parts = partsRegex.matcher(title);
				if (parts.matches()) {
					title = parts.group(1); // before -
				}
				// strip screening type qualifier to collapse films into a movie
				Matcher qualifier = qualifierRegex.matcher(title);
				if (qualifier.matches()) {
					if ("M4J".equals(qualifier.group(1))) {
						// TODO cross-check with advisory
						// TODO cineFilm.setMadeForJuniors(true);
					} else if ("SubM4J".equals(qualifier.group(1))) {
						// TODO cineFilm.setSubtitled(true);
						// cineFilm.setMadeForJuniors(true);
					} else {
						// TODO maybe double-check 2D-3D qualifications
					}
					title = qualifier.group(2); // after (...)
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
