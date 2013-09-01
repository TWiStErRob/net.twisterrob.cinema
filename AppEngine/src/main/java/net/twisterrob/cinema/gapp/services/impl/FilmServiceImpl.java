package net.twisterrob.cinema.gapp.services.impl;

import java.util.*;

import javax.jdo.*;

import net.twisterrob.cinema.PMF;
import net.twisterrob.cinema.gapp.CineworldAccessor;
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
	public List<Film> updateFilms() throws ServiceException {
		try {
			return getFilmsFromCineworld();
		} catch (ApplicationException ex) {
			throw new ServiceException("There has been a problem retrieving Films for %s.", ex, "Cineworld");
		}
	}

	private List<Film> getFilmsFromCineworld() throws ApplicationException {
		PersistenceManager pm = PMF.getPM();
		try {
			List<CineworldFilm> incomingFilms = new CineworldAccessor().getAllFilms();
			List<Film> newFilms = new LinkedList<Film>();
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
						newFilms.add(pm.detachCopy(newFilm));
					}
				} catch (Exception ex) {
					LOG.error("Cannot process film: {} / {}...", incomingFilm.getEdi(), incomingFilm.getTitle(), ex);
				}
			}
			return newFilms;
		} finally {
			pm.close();
		}
	}

	@Override
	public Collection<Film> getAllFilms() throws ServiceException {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			FetchPlan fp = pm.getFetchPlan();
			fp.setGroup(FetchPlan.ALL);
			q = pm.newQuery(Film.class);
			@SuppressWarnings("unchecked")
			List<Film> films = (List<Film>)q.execute();
			return pm.detachCopyAll(films);
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}
}
