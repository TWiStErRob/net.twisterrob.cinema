package com.twister.gapp.cinema;
import java.io.IOException;
import java.util.List;

import javax.jdo.*;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.*;
import com.twister.gapp.PMF;
import com.twister.gapp.cinema.model.*;
import com.twister.gapp.cinema.model.User;

@SuppressWarnings("serial")
public class ListingFilms extends HttpServlet {
	// private static final Logger LOG = LoggerFactory.getLogger(ListingFilms.class);

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

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log();
		setup();
		User user = PMF.getCurrentUser();
		// req.setAttribute("call", new InvokerMap());

		UserService userService = UserServiceFactory.getUserService();
		if (user != null) {
			Object result = getResult(user);
			req.setAttribute("result", result);
			// FIXM
			// http://stackoverflow.com/questions/10036958/the-easiest-way-to-remove-the-bidirectional-recursive-relationships
			req.setAttribute("user", user);
			req.setAttribute("url", userService.createLogoutURL(req.getRequestURI()));
		} else {
			req.setAttribute("url", userService.createLoginURL(req.getRequestURI()));
		}
		RequestDispatcher view = req.getRequestDispatcher("films.jsp");
		view.forward(req, resp);
	}
	private Object getResult(User currentUser) {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			FetchPlan fp = pm.getFetchPlan();
			fp.setGroup(FetchPlan.ALL);
			fp.setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			fp.setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
			fp.setMaxFetchDepth(-1);
			// fp.setDetachmentRootClasses(User.class, View.class, Film.class);
			q = pm.newQuery(User.class);
			q.setFilter("userId == userParam");
			q.declareParameters("String userParam");

			@SuppressWarnings("unchecked")
			List<User> users = (List<User>)q.execute(currentUser.getUserId());
			User user = users.get(0);
			user = pm.detachCopy(user);
			List<View> views = user.getViews();
			return views;
		} finally {
			if (q != null) {
				q.closeAll();
			}
			pm.close();
		}
	}

	private void setup() {
		clear("View");
		clear("Film");
		clear("User");

		addUsers();
		addFilms();
		addViews();
	}

	private void addUsers() {
		addUser("18580476422013912411", "test@example.com", "test");
		addUser("16717695577786171977", "test2@example.com", "test2");
		addUser("12542211391281671681", "test3@example.com", "test3");
	}

	private void addFilms() {
		addFilm(1234L, "World War Z", 120);
		addFilm(1235L, "Smurfs 2", 110);
		addFilm(1236L, "Turbo", 105);
	}

	private void addViews() {
		// test@example.com
		addView("18580476422013912411", 1234L, true, 0.80f);
		addView("18580476422013912411", 1235L, false, 0.75f);
		// test2@example.com
		addView("16717695577786171977", 1234L, false, 0.80f);
		addView("16717695577786171977", 1235L, false, 0.75f);
		addView("16717695577786171977", 1236L, false, 0.99f);
		// test3@example.com
		addView("12542211391281671681", 1234L, true, 0.80f);
		addView("12542211391281671681", 1235L, true, 0.75f);
		addView("12542211391281671681", 1236L, true, 0.99f);
	}

	private void addView(String userId, long edi, boolean seen, float relevant) {
		PersistenceManager pm = PMF.getPM();
		try {
			Film film = pm.getObjectById(Film.class, edi);
			User user = pm.getObjectById(User.class, userId);
			{
				View view = new View();
				{
					view.setFilm(film);
					view.setSeen(seen);
					view.setRelevant(relevant);
				}
				user.addView(view);
			}
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

	private void addUser(String userId, String email, String nickName) {
		PersistenceManager pm = PMF.getPM();
		try {
			User user = new User(userId, email, nickName);
			pm.makePersistent(user);
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