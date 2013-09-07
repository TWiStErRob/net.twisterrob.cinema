package net.twisterrob.cinema.gapp.rest.helpers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.server.internal.process.MappableException;

/**
 * @author Gili Tzabari
 * @see http://stackoverflow.com/a/16552180/253468
 */
//@Provider
//@Singleton
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
	@Override
	public Response toResponse(RuntimeException e) {
		if (e instanceof WebApplicationException) {
			// WORKAROUND: Attempt to mirror Jersey's built-in behavior.
			// @see http://java.net/jira/browse/JERSEY-1607
			WebApplicationException webApplicationException = (WebApplicationException)e;
			return webApplicationException.getResponse();
		}
		// Jetty generates a log entry whenever an exception is thrown. If we don't register an
		// ExceptionMapper, Jersey will log the exception a second time.
		throw new MappableException(e);
	}
}
