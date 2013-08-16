package com.twister.gapp.cinema;
import java.io.IOException;
import java.util.List;

import javax.jdo.*;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.*;
import com.twister.gapp.PMF;
import com.twister.gapp.cinema.model.*;
import com.twister.gapp.cinema.model.User;

@SuppressWarnings("serial")
public class ListingFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(ListingFilms.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		new com.twister.cineworld.model.json.data.CalendarTypeConverter();
		// The results will be passed back (as an attribute) to the JSP view
		// The attribute will be a name/value pair, the value in this case will be a List
		// object
		Object result = getResult();
		req.setAttribute("result", result);
		// req.setAttribute("call", new InvokerMap());

		User user = PMF.getUser();
		UserService userService = UserServiceFactory.getUserService();
		if (user != null) {
			req.setAttribute("user", user);
			req.setAttribute("url", userService.createLogoutURL(req.getRequestURI()));
		} else {
			req.setAttribute("url", userService.createLoginURL(req.getRequestURI()));
		}
		RequestDispatcher view = req.getRequestDispatcher("films.jsp");
		view.forward(req, resp);
	}

	private Object getResult() {
		clear("View");
		clear("Film");
		clear("User");

		User currentUser = PMF.getUser();
		PersistenceManager pm = PMF.getPM();
		try {
			addFilms();
			addViews();
		} catch (Exception ex) {
			LOG.error("Cannot generate data", ex);
		}
		Query q = null;
		try {
			q = pm.newQuery(View.class);
			// q.getFetchPlan().setMaxFetchDepth(FetchPlan.FETCH_SIZE_GREEDY);
			// q.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			q.getFetchPlan().setGroup(FetchPlan.ALL);
			// http://www.datanucleus.org/products/datanucleus/jdo/attach_detach.html
			// q.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
			q.setFilter("user == userParam");
			q.declareParameters("User userParam");

			@SuppressWarnings("unchecked")
			List<View> views = (List<View>)q.execute(currentUser);
			return pm.detachCopyAll(views);
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}

	private void addFilms() {
		addFilm(1234L, "World War Z", 120);
		addFilm(1235L, "Smurfs 2", 110);
		addFilm(1236L, "Turbo", 105);
	}

	private void addViews() {
		User currentUser = PMF.getUser();
		addView(currentUser, 1234L, true, 0.80f);
		addView(currentUser, 1235L, false, 0.75f);
		addView(currentUser, 1236L, false, 0.99f);
	}

	private void addView(User user, long edi, boolean seen, float relevant) {
		PersistenceManager pm = PMF.getPM();
		try {
			View view = new View();
			view.setUser(user);
			view.setSeen(seen);
			view.setRelevant(relevant);
			Film film = pm.getObjectById(Film.class, String.valueOf(edi));
			film.addView(view);
			pm.makePersistent(film);
		} finally {
			pm.close();
		}
	}

	private void addFilm(long edi, String title, int runtime) {
		PersistenceManager pm = PMF.getPM();
		try {
			Film film = new Film(edi, title, runtime);
			pm.makePersistent(film);
		} finally {
			pm.close();
		}

	}

	public void clear(String entityName) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		com.google.appengine.api.datastore.Query cdb = new com.google.appengine.api.datastore.Query(entityName);
		cdb.setKeysOnly();

		Iterable<Entity> results = datastore.prepare(cdb).asIterable();
		for (Entity entity: results) {
			datastore.delete(entity.getKey());
		}
	}
}