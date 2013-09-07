package net.twisterrob.cinema.gapp.rest.config;

import static org.eclipse.persistence.jaxb.MarshallerProperties.*;

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
				.setIncludeRoot(false) // JSON_INCLUDE_ROOT
				.setMarshalEmptyCollections(false) // JSON_MARSHAL_EMPTY_COLLECTIONS
				.marshallerProperty(JSON_WRAPPER_AS_ARRAY_NAME, true) //
				.marshallerProperty(JSON_ATTRIBUTE_PREFIX, "@") //
				.marshallerProperty(INDENT_STRING, "    ") // XXX \t doesn't work?
				.setNamespacePrefixMapper(namespacePrefixMapper) // NAMESPACE_PREFIX_MAPPER
				.setNamespaceSeparator(':') // JSON_NAMESPACE_SEPARATOR
		;
	}

	@Override
	public MoxyJsonConfig getContext(Class<?> objectType) {
		return config;
	}

}
