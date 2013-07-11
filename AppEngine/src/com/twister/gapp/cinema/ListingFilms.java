package com.twister.gapp.cinema;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

@SuppressWarnings("serial") public class ListingFilms extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(ListingFilms.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		new com.twister.cineworld.model.json.data.CalendarTypeConverter();
		// The results will be passed back (as an attribute) to the JSP view
		// The attribute will be a name/value pair, the value in this case will be a List
		// object
		// req.setAttribute("result", result);
		// req.setAttribute("call", new InvokerMap());

		req.setAttribute("hello", "world");
		RequestDispatcher view = req.getRequestDispatcher("films.jsp");
		view.forward(req, resp);

		// UserService userService = UserServiceFactory.getUserService();
		// User user = userService.getCurrentUser();
		//
		// String content = req.getParameter("content");
		// if (content == null) {
		// content = "(No greeting)";
		// }
		// if (user != null) {
		// log.info("Greeting posted by user {}: {}", user.getNickname(), content);
		// } else {
		// log.info("Greeting posted anonymously: {}", content);
		// }
		// resp.sendRedirect("/guestbook.jsp");
	}
}