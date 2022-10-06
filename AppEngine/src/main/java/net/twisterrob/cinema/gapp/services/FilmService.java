package net.twisterrob.cinema.gapp.services;

import java.util.Collection;

import net.twisterrob.cinema.gapp.model.Film;

public interface FilmService {
	public Collection<Film> updateFilms() throws ServiceException;

	public Collection<Film> getFilms() throws ServiceException;
}
