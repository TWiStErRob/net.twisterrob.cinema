package com.twister.gapp.cinema;

import java.io.IOException;
import java.util.*;

import javax.jdo.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.gapp.PMF;
import com.twister.gapp.cinema.model.Film;

@SuppressWarnings("serial")
public class UpdateFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateFilms.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			getFilmsFromCineworld();
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		req.setAttribute("films", getAllFilms());
		RequestDispatcher view = req.getRequestDispatcher("/updateFilms.jsp");
		view.forward(req, resp);
	}

	protected void getFilmsFromCineworld() throws ApplicationException {
		// PMF.clear("Film");
		PersistenceManager pm = PMF.getPM();
		try {
			List<CineworldFilm> films = new CineworldAccessor().getAllFilms();
			for (CineworldFilm film: films) {
				LOG.info("Processing {}: {}...", film.getEdi(), film.getTitle());
				try {
					Film oldFilm;
					try {
						oldFilm = pm.getObjectById(Film.class, film.getEdi());
					} catch (JDOObjectNotFoundException ex) {
						oldFilm = null;
					}
					Film newFilm;
					if (oldFilm != null) {
						newFilm = oldFilm;
						oldFilm.setTitle(film.getTitle());
					} else {
						newFilm = new Film(film.getEdi(), film.getTitle(), -1);
					}
					pm.makePersistent(newFilm);
				} catch (Exception ex) {
					LOG.error("Cannot process film: {} / {}...", film.getEdi(), film.getTitle(), ex);
				}
			}
		} finally {
			pm.close();
		}
	}

	private Collection<Film> getAllFilms() {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
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