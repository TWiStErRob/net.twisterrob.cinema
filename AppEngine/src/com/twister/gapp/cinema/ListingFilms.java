package com.twister.gapp.cinema;
import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.*;

@SuppressWarnings("serial") public class ListingFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(ListingFilms.class.getName());

	private static final String PROP_USER_NAME = "name";
	private static final String PROP_USER_EMAIL = "email";
	private static final String PROP_FILM_TITLE = "title";
	private static final String PROP_FILM_RUNTIME = "runtime";
	private static final String PROP_VIEW_SEEN = "seen";
	private static final String PROP_VIEW_RELEVANT = "relevant";
	private static final String PROP_VIEW_FILM_EDI = "edi";

	private DatastoreService m_datastore = DatastoreServiceFactory.getDatastoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		new com.twister.cineworld.model.json.data.CalendarTypeConverter();
		// The results will be passed back (as an attribute) to the JSP view
		// The attribute will be a name/value pair, the value in this case will be a List
		// object
		Object result = getResult();
		req.setAttribute("result", result);
		// req.setAttribute("call", new InvokerMap());

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
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

		User currentUser = UserServiceFactory.getUserService().getCurrentUser();
		try {
			addUser(currentUser);
			addFilms();
			addViews();
		} catch (Exception ex) {
			LOG.error("Cannot generate data", ex);
		}
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key userKey = KeyFactory.createKey("User", currentUser.getUserId());
		Query query = new Query("View", userKey);// .addSort("date", Query.SortDirection.DESCENDING);
		List<Entity> views = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
		return views;
	}

	private void addFilms() {
		addFilm(1234L, "World War Z", 120);
		addFilm(1235L, "Smurfs 2", 110);
		addFilm(1236L, "Turbo", 105);
	}

	private void addViews() {
		User currentUser = UserServiceFactory.getUserService().getCurrentUser();
		addView(currentUser, 1234L, true, 0.80f);
		addView(currentUser, 1235L, false, 0.75f);
		addView(currentUser, 1236L, false, 0.99f);
	}

	private void addView(User user, long edi, boolean seen, float relevant) {
		Key parentKey = KeyFactory.createKey("User", user.getUserId());
		Entity item = new Entity("View", parentKey);
		item.setProperty(PROP_VIEW_FILM_EDI, edi);
		item.setProperty(PROP_VIEW_SEEN, seen);
		item.setProperty(PROP_VIEW_RELEVANT, relevant);
		m_datastore.put(item);
	}

	private void addFilm(long edi, String title, int runtime) {
		Key entityKey = KeyFactory.createKey("Film", String.valueOf(edi));
		Entity item = new Entity(entityKey);
		item.setProperty(PROP_FILM_TITLE, title);
		item.setProperty(PROP_FILM_RUNTIME, runtime);
		m_datastore.put(item);
	}

	private boolean addUser(User user) {
		if (user == null) {
			return false;
		}
		Key entityKey = KeyFactory.createKey("User", user.getUserId());
		boolean newEntry;
		Entity item;
		try {
			item = m_datastore.get(entityKey);
			newEntry = false;
		} catch (EntityNotFoundException ex) {
			item = new Entity(entityKey);
			newEntry = true;
		}
		item.setProperty(PROP_USER_NAME, new Text(user.getNickname()));
		item.setProperty(PROP_USER_EMAIL, new Text(user.getEmail()));
		m_datastore.put(item);
		return newEntry;
	}

	public void clear(String entityName) {
		Query cdb = new Query(entityName);
		cdb.setKeysOnly();

		Iterable<Entity> results = m_datastore.prepare(cdb).asIterable();
		for (Entity entity: results) {
			m_datastore.delete(entity.getKey());
		}
	}
}