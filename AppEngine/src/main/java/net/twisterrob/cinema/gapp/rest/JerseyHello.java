package net.twisterrob.cinema.gapp.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import net.twisterrob.cinema.gapp.model.View;

import org.glassfish.jersey.server.JSONP;

@Path("/helloworld")
public class JerseyHello {
	@GET
	@JSONP(callback = "callback", queryParam = "callback")
	@Produces({
			MediaType.APPLICATION_JSON, // json
			"application/javascript", "application/x-javascript", "application/ecmascript", "text/javascript",
			"text/x-javascript", "text/ecmascript", "text/jscript" // jsonp
	})
	public View getView() {
		return new View();
	}
}
