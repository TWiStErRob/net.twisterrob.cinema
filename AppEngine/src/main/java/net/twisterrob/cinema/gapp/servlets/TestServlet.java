package net.twisterrob.cinema.gapp.servlets;

import java.io.IOException;

import javax.jdo.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.twisterrob.cinema.gapp.dal.PMF;
import net.twisterrob.cinema.gapp.model.Film;

import org.slf4j.*;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(TestServlet.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOG.info("Hello");
		PersistenceManager pm = PMF.getPM();
		try {
			pm.deletePersistent(pm.getObjectById(Film.class, 62260));
		} catch (JDOObjectNotFoundException ex) {
			throw new ServletException(ex);
		}
	}

	@SuppressWarnings("unused")
	private void log() {
		org.slf4j.Logger slf4j = org.slf4j.LoggerFactory.getLogger(ListingFilms.class);
		slf4j.trace("message-slf4j-trace");
		slf4j.debug("message-slf4j-debug");
		slf4j.info("message-slf4j-info");
		slf4j.warn("message-slf4j-warn");
		slf4j.error("message-slf4j-error");

		java.util.logging.Logger jul = java.util.logging.Logger.getLogger(ListingFilms.class.getName());
		jul.severe("message-jul-severe");
		jul.warning("message-jul-warning");
		jul.info("message-jul-info");
		jul.config("message-jul-config");
		jul.fine("message-jul-fine");
		jul.finer("message-jul-finer");
		jul.finest("message-jul-finest");
	}
}
