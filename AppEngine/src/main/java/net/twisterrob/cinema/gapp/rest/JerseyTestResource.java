package net.twisterrob.cinema.gapp.rest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.JSONP;

import com.google.appengine.api.datastore.*;

@Path("/test")
public class JerseyTestResource {
	@GET
	@JSONP(callback = "callback", queryParam = "callback")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/javascript"})
	public Key test() {
		Key parent = KeyFactory.createKey("SubKey", 12345);
		Key child = KeyFactory.createKey(parent, "SuperKey", "KeyName");
		return child;
	}
}
