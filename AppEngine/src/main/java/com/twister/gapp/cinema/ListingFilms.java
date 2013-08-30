package com.twister.gapp.cinema;
import java.io.IOException;
import java.util.List;

import javax.jdo.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.appengine.api.users.*;
import com.twister.gapp.PMF;
import com.twister.gapp.cinema.model.*;
import com.twister.gapp.cinema.model.User;

@SuppressWarnings("serial")
public class ListingFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(ListingFilms.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		setup();
		User user = PMF.getCurrentUser();
		// req.setAttribute("call", new InvokerMap());

		UserService userService = UserServiceFactory.getUserService();
		if (user != null) {
			Object result = getResult(user);
			req.setAttribute("views", result);
			// FIXM
			// http://stackoverflow.com/questions/10036958/the-easiest-way-to-remove-the-bidirectional-recursive-relationships
			req.setAttribute("user", user);
			req.setAttribute("url", userService.createLogoutURL(req.getRequestURI()));
		} else {
			req.setAttribute("url", userService.createLoginURL(req.getRequestURI()));
		}
		RequestDispatcher view = req.getRequestDispatcher("/films.jsp");
		view.forward(req, resp);
	}
	private Object getResult(User currentUser) {
		LOG.debug("Getting views for user: {}", currentUser.getUserId());
		PersistenceManager pm = PMF.getPM();
		try {
			FetchPlan fp = pm.getFetchPlan();
			fp.setGroup(FetchPlan.ALL);
			fp.setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			fp.setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
			fp.setMaxFetchDepth(-1);

			User user = pm.getObjectById(User.class, currentUser.getUserId());
			user = pm.detachCopy(user);
			List<View> views = user.getViews();
			return views;
		} finally {
			pm.close();
		}
	}

	private void setup() {
		PMF.clear("View");
		PMF.clear("User");

		addUsers();
		addViews();
	}

	private void addUsers() {
		addUser("18580476422013912411", "test@example.com", "test");
		addUser("16717695577786171977", "test2@example.com", "test2");
		addUser("12542211391281671681", "test3@example.com", "test3");
	}

	private void addViews() {
		// test@example.com
		addView("18580476422013912411", 148839L, true, 0.80f);
		addView("18580476422013912411", 67754L, false, 0.75f);
		// test2@example.com
		addView("16717695577786171977", 148839L, false, 0.80f);
		addView("16717695577786171977", 67754L, false, 0.75f);
		addView("16717695577786171977", 134988L, false, 0.99f);
		// test3@example.com
		addView("12542211391281671681", 148839L, true, 0.80f);
		addView("12542211391281671681", 67754L, true, 0.75f);
		addView("12542211391281671681", 134988L, true, 0.99f);
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

	private void addUser(String userId, String email, String nickName) {
		PersistenceManager pm = PMF.getPM();
		try {
			User user = new User(userId, email, nickName);
			pm.makePersistent(user);
		} finally {
			pm.close();
		}
	}
}