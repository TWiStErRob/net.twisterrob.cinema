package net.twisterrob.cinema.gapp.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import net.twisterrob.cinema.gapp.dal.PMF;
import net.twisterrob.cinema.gapp.model.Film;
import net.twisterrob.cinema.gapp.services.FilmService;

import org.glassfish.jersey.server.JSONP;
import org.slf4j.*;

@Path("/films")
public class FilmsResource {
	private static final Logger LOG = LoggerFactory.getLogger(FilmsResource.class);

	@Inject
	private FilmService service;

	@GET
	@Path("/update")
	@JSONP(callback = "callback", queryParam = "callback")
	@Produces({
			MediaType.APPLICATION_JSON, // json
			"application/javascript", "application/x-javascript", "application/ecmascript", "text/javascript",
			"text/x-javascript", "text/ecmascript", "text/jscript" // jsonp
	})
	public Collection<Film> updateFilms(@QueryParam("clean") @DefaultValue("false") boolean clean) throws Exception {
		if (clean) {
			LOG.debug("Cleaning all entities: {}.", Film.class);
			PMF.clear(Film.class);
		}
		Collection<Film> newFilms = service.updateFilms();
		return newFilms;
	}
}
