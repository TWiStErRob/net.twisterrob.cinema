package com.twister.gapp.cinema.rest;

import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.*;

import javax.jdo.JDOObjectNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.*;

@Provider
public class NotFoundMapper implements ExceptionMapper<JDOObjectNotFoundException> {
	@Override
	public Response toResponse(JDOObjectNotFoundException e) {
		return status(NOT_FOUND).entity(e.getMessage()).build();
	}
}
