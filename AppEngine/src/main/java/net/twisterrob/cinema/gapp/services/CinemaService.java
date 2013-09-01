package net.twisterrob.cinema.gapp.services;

import java.util.Collection;

import net.twisterrob.cinema.gapp.model.Cinema;

public interface CinemaService {
	public Collection<Cinema> updateCinemas() throws ServiceException;

	public Collection<Cinema> getAllCinemas() throws ServiceException;
}
