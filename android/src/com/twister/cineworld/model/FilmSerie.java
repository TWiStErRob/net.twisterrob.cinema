package com.twister.cineworld.model;

import java.util.*;

public class FilmSerie extends FilmBase {
	private final List<Film>	m_films	= new LinkedList<Film>();

	public void add(final Film film) {
		m_films.add(film);
	}

	public List<Film> getFilms() {
		return Collections.unmodifiableList(m_films);
	}

	@Override
	public boolean has2D() {
		for (Film film : m_films) {
			if (film.has2D()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean has3D() {
		for (Film film : m_films) {
			if (film.has3D()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasIMax2D() {
		for (Film film : m_films) {
			if (film.hasIMax2D()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasIMax3D() {
		for (Film film : m_films) {
			if (film.hasIMax3D()) {
				return true;
			}
		}
		return false;
	}

	public FilmBase toFilm() {
		if (m_films.size() == 1) {
			return m_films.get(0);
		} else {
			return this;
		}
	}
}
