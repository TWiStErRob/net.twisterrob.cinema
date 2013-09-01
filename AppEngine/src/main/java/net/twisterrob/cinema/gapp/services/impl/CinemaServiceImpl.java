package net.twisterrob.cinema.gapp.services.impl;

import java.util.*;

import javax.jdo.*;

import net.twisterrob.cinema.PMF;
import net.twisterrob.cinema.gapp.CineworldAccessor;
import net.twisterrob.cinema.gapp.model.Cinema;
import net.twisterrob.cinema.gapp.rest.CinemasResource;
import net.twisterrob.cinema.gapp.services.*;

import org.joda.time.DateTime;
import org.slf4j.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.json.data.CineworldCinema;

public class CinemaServiceImpl implements CinemaService {
	private static final Logger LOG = LoggerFactory.getLogger(CinemasResource.class);

	@Override
	public List<Cinema> updateCinemas() throws ServiceException {
		try {
			return getCinemasFromCineworld();
		} catch (ApplicationException ex) {
			throw new ServiceException("There has been a problem retrieving Cinemas for %s.", ex, "Cineworld");
		}
	}

	private List<Cinema> getCinemasFromCineworld() throws ApplicationException {
		PersistenceManager pm = PMF.getPM();
		try {
			List<CineworldCinema> incomingCinemas = new CineworldAccessor().getAllCinemas();
			List<Cinema> newCinemas = new LinkedList<Cinema>();
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
						newCinema.setUrl(incomingCinema.getCinemaUrl());
						newCinema.setAddress(incomingCinema.getAddress());
						newCinema.setPostcode(incomingCinema.getPostcode());
						newCinema.setTelephone(incomingCinema.getTelephone());
						pm.makePersistent(newCinema);
						newCinemas.add(pm.detachCopy(newCinema));
					}
				} catch (Exception ex) {
					LOG.error("Cannot process Cinema: {} / {}...", incomingCinema.getId(), incomingCinema.getName(), ex);
				}
			}
			return newCinemas;
		} finally {
			pm.close();
		}
	}

	@Override
	public Collection<Cinema> getAllCinemas() throws ServiceException {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			FetchPlan fp = pm.getFetchPlan();
			fp.setGroup(FetchPlan.ALL);
			q = pm.newQuery(Cinema.class);
			@SuppressWarnings("unchecked")
			List<Cinema> Cinemas = (List<Cinema>)q.execute();
			return pm.detachCopyAll(Cinemas);
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}
}
