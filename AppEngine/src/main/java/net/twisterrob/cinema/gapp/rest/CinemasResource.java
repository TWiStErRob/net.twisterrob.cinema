package net.twisterrob.cinema.gapp.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.*;

import net.twisterrob.cinema.gapp.dal.PMF;
import net.twisterrob.cinema.gapp.model.*;
import net.twisterrob.cinema.gapp.rest.model.*;
import net.twisterrob.cinema.gapp.rest.model.RestResponse.ServiceStatus;
import net.twisterrob.cinema.gapp.services.*;

import org.glassfish.jersey.server.JSONP;
import org.slf4j.*;

@Path("/cinemas")
// TODO serve both JSON and JSONP
public class CinemasResource {
	private static final Logger LOG = LoggerFactory.getLogger(CinemasResource.class);

	@Inject
	private CinemaService service;

	@GET
	@Path("/update")
	@Produces("application/javascript")
	@JSONP(callback = "callback", queryParam = "callback")
	public Collection<Cinema> updateCinemas(@QueryParam("clean") @DefaultValue("false") boolean clean)
			throws ServiceException {
		if (clean) {
			LOG.debug("Cleaning all entities: {}.", Cinema.class);
			PMF.clear(Cinema.class);
		}
		Collection<Cinema> newCinemas = service.updateCinemas();
		return newCinemas;
	}

	@GET
	@Path("/favorite")
	@Produces("application/javascript")
	@JSONP(callback = "callback", queryParam = "callback")
	public RestResponse favoriteCinema(@QueryParam("cinema") long cinema, @QueryParam("rating") short rating,
			@QueryParam("displayOrder") int displayOrder) throws ServiceException {
		FavoriteCinema favoriteCinema = service.favoriteCinema(cinema, rating, displayOrder);
		RestResponse entity = new RestResponse(ServiceStatus.Success, favoriteCinema,
				"Successfully favorited %d with rating %d%%", cinema, rating);
		return entity;
	}
	@GET
	@Path("/favorites")
	@Produces("application/javascript")
	@JSONP(callback = "callback", queryParam = "callback")
	public Collection<FavoriteCinema> getFavorites() throws ServiceException {
		return service.getFavorites();
	}
}
