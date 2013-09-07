package net.twisterrob.cinema.gapp.services.impl;

import java.util.*;

import javax.jdo.*;

import net.twisterrob.cinema.gapp.dal.*;
import net.twisterrob.cinema.gapp.model.*;
import net.twisterrob.cinema.gapp.rest.CinemasResource;
import net.twisterrob.cinema.gapp.services.*;

import org.joda.time.DateTime;
import org.slf4j.*;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableMap;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.json.data.CineworldCinema;

public class CinemaServiceImpl implements CinemaService {
	private static final Logger LOG = LoggerFactory.getLogger(CinemasResource.class);

	@Override
	public Collection<Cinema> updateCinemas() throws ServiceException {
		try {
			return getCinemasFromCineworld();
		} catch (ApplicationException ex) {
			throw new ServiceException("There has been a problem retrieving Cinemas for %s.", ex, "Cineworld");
		}
	}

	private Collection<Cinema> getCinemasFromCineworld() throws ApplicationException {
		PersistenceManager pm = PMF.getPM();
		try {
			List<CineworldCinema> incomingCinemas = new CineworldAccessor().getAllCinemas();
			Collection<Cinema> newCinemas = new LinkedList<Cinema>();
			for (CineworldCinema incomingCinema: incomingCinemas) {
				LOG.info("Processing {}: {}...", incomingCinema.getId(), incomingCinema.getName());
				try {
					Cinema oldCinema;
					try {
						oldCinema = pm.getObjectById(Cinema.class, incomingCinema.getId());
					} catch (JDOObjectNotFoundException ex) {
						oldCinema = null;
					}
					Cinema newCinema;
					if (oldCinema != null) {
						oldCinema.setLastUpdated(new DateTime());
					} else {
						newCinema = new Cinema(incomingCinema.getId(), incomingCinema.getName());
						{
							newCinema.setUrl(incomingCinema.getCinemaUrl());
							newCinema.setAddress(incomingCinema.getAddress());
							newCinema.setPostcode(incomingCinema.getPostcode());
							newCinema.setTelephone(incomingCinema.getTelephone());
						}
						pm.makePersistent(newCinema);
						newCinemas.add(newCinema);
					}
				} catch (Exception ex) {
					LOG.error("Cannot process Cinema: {} / {}...", incomingCinema.getId(), incomingCinema.getName(), ex);
				}
			}
			newCinemas = pm.detachCopyAll(newCinemas);
			return newCinemas;
		} finally {
			pm.close();
		}
	}

	@Override
	public FavoriteCinema favoriteCinema(long cinemaId, short rating, int displayOrder) throws ServiceException {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			User user = pm.getObjectById(User.class, PMF.getCurrentUserId());
			Cinema cinema = pm.getObjectById(Cinema.class, cinemaId);
			q = pm.newQuery(FavoriteCinema.class);
			q.getFetchPlan().setGroup(FetchPlan.ALL);
			q.setFilter("(this.user == :userId) && (this.cinema == :cinemaId)");
			@SuppressWarnings("unchecked")
			Collection<FavoriteCinema> favs = (Collection<FavoriteCinema>)q.executeWithMap(ImmutableMap.builder() //
					.put("userId", KeyFactory.createKey(User.class.getSimpleName(), user.getUserId())) //
					.put("cinemaId", KeyFactory.createKey(Cinema.class.getSimpleName(), cinema.getId())) //
					.build());

			FavoriteCinema fav;
			if (favs.size() == 0) {
				fav = new FavoriteCinema();
				fav.setCinema(cinema);
				user.addFavoriteCinema(fav);
			} else if (favs.size() == 1) {
				fav = favs.iterator().next();
			} else {
				throw new ServiceException("Multiple favorites exist for user %s and cinema %d (%s)", //
						user.getUserId(), cinema.getId(), cinema.getName());
			}

			fav.setRating(rating);
			fav.setDisplayOrder(displayOrder);
			pm.makePersistent(fav);
			fav = pm.detachCopy(fav);
			return fav;
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}

	@Override
	public Collection<FavoriteCinema> getFavorites() throws ServiceException {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			q = pm.newQuery(FavoriteCinema.class);
			q.getFetchPlan().setGroup(FetchPlan.ALL);
			q.setFilter("this.user == :userId");
			@SuppressWarnings("unchecked")
			Collection<FavoriteCinema> favs = (Collection<FavoriteCinema>)q.executeWithMap(ImmutableMap.builder() //
					.put("userId", KeyFactory.createKey(User.class.getSimpleName(), PMF.getCurrentUserId())) //
					.build());
			favs = pm.detachCopyAll(favs);
			return favs;
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}

	@Override
	public Collection<Cinema> getCinemas() throws ServiceException {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			FetchPlan fp = pm.getFetchPlan();
			fp.setGroup(FetchPlan.ALL);
			q = pm.newQuery(Cinema.class);
			@SuppressWarnings("unchecked")
			Collection<Cinema> cinemas = (Collection<Cinema>)q.execute();
			cinemas = pm.detachCopyAll(cinemas);
			return cinemas;
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}
}
