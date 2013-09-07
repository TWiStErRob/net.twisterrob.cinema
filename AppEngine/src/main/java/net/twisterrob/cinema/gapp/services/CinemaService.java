package net.twisterrob.cinema.gapp.services;

import java.util.Collection;

import net.twisterrob.cinema.gapp.model.*;

public interface CinemaService {
	public Collection<Cinema> updateCinemas() throws ServiceException;

	public Collection<Cinema> getCinemas() throws ServiceException;

	public FavoriteCinema favoriteCinema(long cinema, short rating, int displayOrder) throws ServiceException;

	public Collection<FavoriteCinema> getFavorites() throws ServiceException;
}
