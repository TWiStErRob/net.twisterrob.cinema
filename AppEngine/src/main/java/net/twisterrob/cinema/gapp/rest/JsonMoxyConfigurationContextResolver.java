package net.twisterrob.cinema.gapp.rest;

import java.util.*;

import javax.ws.rs.ext.*;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

@Provider
public class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {

	private final MoxyJsonConfig config;

	public JsonMoxyConfigurationContextResolver() {
		final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();
		namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		namespacePrefixMapper.put("http://appengine.google.com/datastore", "google-ds");

		config = new MoxyJsonConfig() //
				.setFormattedOutput(true) //
				.setIncludeRoot(false) //
				.setMarshalEmptyCollections(true) //
				.setNamespacePrefixMapper(namespacePrefixMapper) //
				.setNamespaceSeparator(':');
	}

	@Override
	public MoxyJsonConfig getContext(Class<?> objectType) {
		return config;
	}

}
