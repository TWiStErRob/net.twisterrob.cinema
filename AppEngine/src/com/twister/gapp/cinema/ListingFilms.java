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
		setup();
		User user = PMF.getCurrentUser();
		Object result = getResult(user);
		req.setAttribute("result", result);
		// req.setAttribute("call", new InvokerMap());

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

	private Object getResult(User currentUser) {
		PersistenceManager pm = PMF.getPM();
		Query q = null;
		try {
			q = pm.newQuery(View.class);
			q.getFetchPlan().setGroup(FetchPlan.ALL);
			q.setFilter("user == userParam");
			q.declareParameters(User.class.getName() + " userParam");

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
		addView("18580476422013912411", 1236L, false, 0.99f);
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
			User user = pm.getObjectById(User.class, userId);
			Film film = pm.getObjectById(Film.class, edi);
			{
				View view = new View();
				{
					view.setUser(user);
					view.setSeen(seen);
					view.setRelevant(relevant);
				}
				film.addView(view);
			}
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