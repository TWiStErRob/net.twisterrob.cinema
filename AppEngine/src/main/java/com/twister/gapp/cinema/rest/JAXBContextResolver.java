package com.twister.gapp.cinema.rest;

import javax.ws.rs.ext.*;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.*;
import com.twister.gapp.cinema.model.Dateable;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	private JAXBContext context;
	private Class<?>[] types = {Dateable.class};

	public JAXBContextResolver() throws Exception {
		JSONConfiguration jsonConfiguration = JSONConfiguration.natural() //
				.humanReadableFormatting(true) //
				// don't use */ .usePrefixesAtNaturalAttributes() //
				.build();
		this.context = new JSONJAXBContext(jsonConfiguration, types);
	}
	public JAXBContext getContext(Class<?> objectType) {
		for (Class<?> type: types) {
			if (type == objectType) {
				return context;
			}
		}
		return null;
	}
}