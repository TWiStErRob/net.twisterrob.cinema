package net.twisterrob.cinema.gapp.services.impl;

import java.util.*;

import javax.jdo.*;

import net.twisterrob.cinema.gapp.dal.*;
import net.twisterrob.cinema.gapp.model.Film;
import net.twisterrob.cinema.gapp.rest.FilmsResource;
import net.twisterrob.cinema.gapp.services.*;

import org.joda.time.DateTime;
import org.slf4j.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.json.data.CineworldFilm;

public class FilmServiceImpl implements FilmService {
	private static final Logger LOG = LoggerFactory.getLogger(FilmsResource.class);

	@Override
	public Collection<Film> updateFilms() throws ServiceException {
		try {
			return updateFilmsFromCineworld();
		} catch (ApplicationException ex) {
			throw new ServiceException("There has been a problem retrieving Films for %s.", ex, "Cineworld");
		}
	}

	private Collection<Film> updateFilmsFromCineworld() throws ApplicationException {
		PersistenceManager pm = PMF.getPM();
		try {
			List<CineworldFilm> incomingFilms = new CineworldAccessor().getAllFilms();
			Collection<Film> newFilms = new LinkedList<>();
			for (CineworldFilm incomingFilm: incomingFilms) {
				LOG.info("Processing {}: {}...", incomingFilm.getEdi(), incomingFilm.getTitle());
				try {
					Film oldFilm;
					try {
						oldFilm = pm.getObjectById(Film.class, incomingFilm.getEdi());
					} catch (JDOObjectNotFoundException ex) {
						oldFilm = null;
					}
					Film newFilm;
					if (oldFilm != null) {
						oldFilm.setLastUpdated(new DateTime());
					} else {
						newFilm = new Film(incomingFilm.getEdi(), incomingFilm.getTitle(), -1);
						pm.makePersistent(newFilm);
						newFilms.add(newFilm);
					}
				} catch (Exception ex) {
					LOG.error("Cannot process film: {} / {}...", incomingFilm.getEdi(), incomingFilm.getTitle(), ex);
				}
			}
			newFilms = pm.detachCopyAll(newFilms);
			return newFilms;
		} finally {
			pm.close();
		}
	}

	@Override
	public Collection<Film> getFilms() throws ServiceException {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			FetchPlan fp = pm.getFetchPlan();
			fp.setGroup(FetchPlan.ALL);
			q = pm.newQuery(Film.class);
			@SuppressWarnings("unchecked")
			Collection<Film> films = (Collection<Film>)q.execute();
			return pm.detachCopyAll(films);
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}
}
