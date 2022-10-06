package net.twisterrob.cinema.gapp.rest.helpers;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.*;

import org.slf4j.*;

/**
 * Internal Server Error 500 is not logged, log it here.
 * 
 * @author TWiStEr
 * 
 * @see org.glassfish.jersey.server.ServerRuntime.Responder#mapException(Throwable)
 */
@Provider
@Singleton
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
	private static final Logger LOG = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

	@Override
	public Response toResponse(WebApplicationException ex) {
		Response response = ex.getResponse();
		LOG.error(response.toString(), ex);
		StatusType status = response.getStatusInfo();
		if (ex.getClass() == WebApplicationException.class) {
			// If we got a generic WebApplicationExcetion, put the error message in the response header
			status = new OverrideStatusReason(status, ex.getCause().toString());
		}
		return Response.fromResponse(response).status(status).build();
	}
}
