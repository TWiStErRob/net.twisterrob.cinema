package net.twisterrob.cinema.gapp.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import net.twisterrob.cinema.PMF;
import net.twisterrob.cinema.gapp.model.Cinema;
import net.twisterrob.cinema.gapp.services.CinemaService;

import org.glassfish.jersey.server.JSONP;
import org.slf4j.*;

@Path("/cinemas")
public class CinemasResource {
	private static final Logger LOG = LoggerFactory.getLogger(CinemasResource.class);

	@Inject
	private CinemaService service;

	@GET
	@Path("/update")
	@JSONP(callback = "callback", queryParam = "callback")
	@Produces({
			MediaType.APPLICATION_JSON, // json
			"application/javascript", "application/x-javascript", "application/ecmascript", "text/javascript",
			"text/x-javascript", "text/ecmascript", "text/jscript" // jsonp
	})
	public Collection<Cinema> updateCinemas(@QueryParam("clean") @DefaultValue("false") boolean clean) throws Exception {
		if (clean) {
			LOG.debug("Cleaning all entities: {}.", Cinema.class);
			PMF.clear(Cinema.class);
		}
		Collection<Cinema> newFilms = service.updateCinemas();
		return newFilms;
	}
}
