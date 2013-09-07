package net.twisterrob.cinema.gapp.rest.helpers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * @author Jin Kwon
 * @author TWiStEr
 * @see http://stackoverflow.com/a/18371159/253468
 */
public class OverrideStatusReason implements StatusType {

	public OverrideStatusReason(final StatusType status, final String reasonPhrase) {
		this(status.getFamily(), status.getStatusCode(), reasonPhrase);
	}

	public OverrideStatusReason(final Family family, final int statusCode, final String reasonPhrase) {
		super();

		this.family = family;
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	@Override
	public Family getFamily() {
		return family;
	}

	@Override
	public String getReasonPhrase() {
		return reasonPhrase;
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	public ResponseBuilder responseBuilder() {
		return Response.status(this);
	}

	public Response build() {
		return responseBuilder().build();
	}

	public WebApplicationException except() {
		return new WebApplicationException(build());
	}

	private final Family family;
	private final int statusCode;
	private final String reasonPhrase;
}
