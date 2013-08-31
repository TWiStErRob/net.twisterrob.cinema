package com.twister.gapp.cinema.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.JSONP;

import com.twister.gapp.cinema.model.View;
import com.twister.gapp.cinema.services.*;

@Path("/helloworld")
public class JerseyHello {
	private ViewService service = new ViewServiceImpl();

	@GET
	@JSONP(callback = "callback", queryParam = "callback")
	@Produces({
			MediaType.APPLICATION_JSON, // json
			"application/javascript", "application/x-javascript", "application/ecmascript", "text/javascript",
			"text/x-javascript", "text/ecmascript", "text/jscript" // jsonp
	})
	public View getView() {
		return service.getView();
	}
}
