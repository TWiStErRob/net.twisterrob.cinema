package com.twister.cineworld.ui;

import java.util.*;

import com.twister.cineworld.model.*;
import com.twister.cineworld.model.json.data.CineworldFilm;

public class FilmMatcher {
	public List<Film> match(final List<CineworldFilm> cineFilms) {
		List<Film> films = new LinkedList<Film>();
		for (CineworldFilm cineFilm : cineFilms) {
			String[] parts = cineFilm.getTitle().split("\\s*-\\s*", 2);
			// Log.d("MATCH", Arrays.toString(parts));
			String title = parts.length == 1? parts[0] : parts[1];
			Film film = find(films, title);
			if (film == null) {
				film = new Film();
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

	public List<FilmBase> matchSeries(final List<Film> films) {
		List<FilmSerie> filmSeries = new LinkedList<FilmSerie>();
		for (Film film : films) {
			String[] parts = film.getTitle().split("\\s*:\\s*", 2);
			// Log.d("MATCH", Arrays.toString(parts));
			String title = parts[0]; // either the whole or the basic
			FilmSerie filmSerie = findSerie(filmSeries, title);
			if (filmSerie == null) {
				filmSerie = new FilmSerie();
				filmSerie.setTitle(title);
				filmSerie.setClassification(film.getClassification());
				filmSerie.setAdvisory(film.getAdvisory());
			}
			filmSerie.add(film);
			if (findSerie(filmSeries, filmSerie.getTitle()) == null) {
				filmSeries.add(filmSerie);
			}
		}
		List<FilmBase> filmList = new ArrayList<FilmBase>(filmSeries.size());
		for (FilmSerie filmSerie : filmSeries) {
			filmList.add(filmSerie.toFilm());
		}
		return filmList;
	}

	private FilmSerie findSerie(final List<FilmSerie> filmSeries, final String string) {
		for (FilmSerie filmSerie : filmSeries) {
			if (filmSerie.getTitle().equals(string)) {
				return filmSerie;
			}
		}
		return null;
	}

	private Film find(final List<Film> films, final String string) {
		for (Film film : films) {
			if (film.getTitle().equals(string)) {
				return film;
			}
		}
		return null;
	}
}
