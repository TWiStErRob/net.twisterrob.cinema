package com.twister.gapp.cinema.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.json.JSONWithPadding;
import com.twister.gapp.cinema.model.View;
import com.twister.gapp.cinema.services.ViewService;

@Path("/helloworld")
public class JerseyHello {
	@Context
	private ViewService service;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public View getView() {
		return service.getView();
	}

	@GET
	@Path("/jsonp")
	@Produces("application/x-javascript")
	public JSONWithPadding getViewJSONP(@QueryParam("callback") @DefaultValue("callback") String callback) {
		return new JSONWithPadding(getView(), callback);
	}
}
