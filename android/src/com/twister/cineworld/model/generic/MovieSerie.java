package com.twister.cineworld.model.generic;

import java.util.*;

public class MovieSerie extends MovieBase {
	private static final long	serialVersionUID	= -8408653139231537313L;

	private final List<Movie>	m_films				= new LinkedList<Movie>();

	public void add(final Movie film) {
		m_films.add(film);
	}

	public List<Movie> getFilms() {
		return Collections.unmodifiableList(m_films);
	}

	@Override
	public boolean has2D() {
		for (Movie film : m_films) {
			if (film.has2D()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean has3D() {
		for (Movie film : m_films) {
			if (film.has3D()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasIMax2D() {
		for (Movie film : m_films) {
			if (film.hasIMax2D()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasIMax3D() {
		for (Movie film : m_films) {
			if (film.hasIMax3D()) {
				return true;
			}
		}
		return false;
	}

	public MovieBase toFilm() {
		if (m_films.size() == 1) {
			return m_films.get(0);
		} else {
			return this;
		}
	}
}
