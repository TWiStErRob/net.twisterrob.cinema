package com.twister.cineworld.model.json;

import java.io.*;
import java.net.*;

import org.slf4j.*;

import com.google.gson.*;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.exception.ExternalException.System;
import com.twister.cineworld.tools.IOTools;

public class JavaNetURLJsonClient implements JsonClient {
	private static final Logger LOG = LoggerFactory.getLogger(JavaNetURLJsonClient.class);
	private final Gson m_gson;

	public JavaNetURLJsonClient(final Gson gson) {
		if (gson == null) throw new NullPointerException("Gson must be supplied");
		m_gson = gson;
	}
	@Override public <T> T get(URL url, Class<T> responseType) throws ApplicationException {
		String json = null;
		try {
			LOG.debug("Retrieving url... {}, expecting {}", url, responseType);
			json = retrieve(url);

			T object = m_gson.fromJson(json, responseType);
			return object;
		} catch (MalformedURLException ex) {
			throw new InternalException("Invalid URL: %s", ex, url);
		} catch (IOException ex) {
			throw new ExternalException(System.CINEWORLD, "Cannot retrieve data", ex);
		} catch (JsonParseException ex) {
			LOG.debug(json);
			throw ex;
		}
	}
	protected String retrieve(URL url) throws ApplicationException, IOException {
		Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
		String json = IOTools.readAll(reader);
		return json;
	}

	@Override public <T> T post(String url, Object requestObject, Class<T> responseType)
			throws ApplicationException {
		throw new UnsupportedOperationException("Only GET is supported");
	}

}
