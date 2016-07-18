package net.twisterrob.cinema.gapp.servlets;
import java.io.IOException;
import java.util.*;

import javax.jdo.*;
import javax.servlet.*;
import javax.servlet.http.*;

import net.twisterrob.cinema.gapp.dal.PMF;
import net.twisterrob.cinema.gapp.model.*;
import net.twisterrob.cinema.gapp.model.User;
import net.twisterrob.cinema.gapp.services.impl.FilmServiceImpl;

import org.joda.time.DateTime;
import org.slf4j.*;

import com.google.appengine.api.users.*;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("serial")
public class ListingFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(ListingFilms.class);

	@Override
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

		try {
			Collection<Film> oldFilms = new FilmServiceImpl().getFilms();
			Collection<Film> newFilms = new ArrayList<>();
			DateTime newAfter = new DateTime().minusDays(1);
			for (Iterator<Film> it = oldFilms.iterator(); it.hasNext();) {
				Film film = it.next();
				if (newAfter.compareTo(film.getCreated()) <= 0) { // newAfter <= film.created
					it.remove();
					newFilms.add(film);
				}
			}
			req.setAttribute("films", ImmutableMap.<String, Collection<Film>> builder() //
					.put("new", newFilms) //
					.put("existing", oldFilms) //
					.build());
		} catch (Exception ex) {
			throw new ServletException(ex);
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
			Collection<View> views = user.getViews();
			return views;
		} finally {
			pm.close();
		}
	}

	private Collection<Film> getThreeFilms() {
		PersistenceManager pm = PMF.getPM();
		try {
			Query q = pm.newQuery(Film.class);
			q.setRange(0, 3);
			@SuppressWarnings("unchecked")
			Collection<Film> films = (Collection<Film>)q.execute();
			return pm.detachCopyAll(films);
		} finally {
			pm.close();
		}
	}

	private void setup() {
		PMF.clear("View");
		PMF.clear("User");

		addUsers();
		Collection<Film> films = getThreeFilms();
		Iterator<Film> it = films.iterator();
		addViews(it.next(), it.next(), it.next());
	}

	private void addUsers() {
		addUser("18580476422013912411", "test@example.com", "test");
		addUser("16717695577786171977", "test2@example.com", "test2");
		addUser("12542211391281671681", "test3@example.com", "test3");
	}

	private void addViews(Film f1, Film f2, Film f3) {
		// test@example.com
		addView("18580476422013912411", f1.getEdi(), true, 0.80f);
		addView("18580476422013912411", f2.getEdi(), false, 0.75f);
		// test2@example.com
		addView("16717695577786171977", f1.getEdi(), false, 0.80f);
		addView("16717695577786171977", f2.getEdi(), false, 0.75f);
		addView("16717695577786171977", f3.getEdi(), false, 0.99f);
		// test3@example.com
		addView("12542211391281671681", f1.getEdi(), true, 0.80f);
		addView("12542211391281671681", f2.getEdi(), true, 0.75f);
		addView("12542211391281671681", f3.getEdi(), true, 0.99f);
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