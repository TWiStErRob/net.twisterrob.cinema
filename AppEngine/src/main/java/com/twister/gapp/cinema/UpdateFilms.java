package com.twister.gapp.cinema;

import java.io.IOException;
import java.util.*;

import javax.jdo.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.joda.time.DateTime;
import org.slf4j.*;

import com.google.common.collect.ImmutableMap;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.gapp.PMF;
import com.twister.gapp.cinema.model.Film;

@SuppressWarnings("serial")
public class UpdateFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateFilms.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (Boolean.parseBoolean(req.getParameter("clean"))) {
			PMF.clear(Film.class);
		}
		try {
			List<Film> newFilms = getFilmsFromCineworld();
			Collection<Film> allFilms = getAllFilms();
			for (Film film: newFilms) {
				allFilms.remove(film);
			}
			req.setAttribute("films", ImmutableMap.<String, Collection<Film>> builder() //
					.put("new", newFilms) //
					.put("existing", allFilms) //
					.build());
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		RequestDispatcher view = req.getRequestDispatcher("/updateFilms.jsp");
		view.forward(req, resp);
	}

	protected List<Film> getFilmsFromCineworld() throws ApplicationException {
		// PMF.clear("Film");
		PersistenceManager pm = PMF.getPM();
		// try {
		// pm.deletePersistent(pm.getObjectById(Film.class, 44064));
		// } catch (JDOObjectNotFoundException ex) {}
		try {
			List<CineworldFilm> incomingFilms = new CineworldAccessor().getAllFilms();
			List<Film> newFilms = new LinkedList<Film>();
			for (CineworldFilm incomingFilm: incomingFilms) {
				// if (!incomingFilm.getTitle().startsWith("3D -")) {
				// continue;
				// }
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

	private Collection<Film> getAllFilms() {
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