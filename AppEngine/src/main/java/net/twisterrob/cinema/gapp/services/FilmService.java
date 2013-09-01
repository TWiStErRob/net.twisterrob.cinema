package net.twisterrob.cinema.gapp.services;

import java.util.Collection;

import net.twisterrob.cinema.gapp.model.*;

public interface FilmService {
	public Collection<Film> updateFilms(Vendor vendor) throws ServiceException;

	public Collection<Film> getAllFilms(Vendor vendor) throws ServiceException;
}
