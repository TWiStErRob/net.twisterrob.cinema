package com.twister.gapp;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class GlobalFilter implements Filter {
	@Override public void init(FilterConfig config) throws ServletException {}

	@Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String path = req.getRequestURI().substring(req.getContextPath().length());

		if (path.startsWith("/global")) {
			resp.sendRedirect("http://twisterrob.net" + path);
			// request.getRequestDispatcher("/global" + path).forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override public void destroy() {}
}
