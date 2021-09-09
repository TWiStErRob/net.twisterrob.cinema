package com.twister.cineworld.model.json;

import java.io.*;
import java.net.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;

import android.net.http.AndroidHttpClient;

import com.google.gson.*;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.log.*;
import com.twister.cineworld.tools.IOTools;

public class ApacheHttpJsonClient implements JsonClient {
	private static final Log	LOG					= LogFactory.getLog(Tag.JSON);
	private static final String	HEADER_CONTENT_TYPE	= "Content-type";
	private static final String	HEADER_ACCEPT		= "Accept";
	private static final String	CONTENT_TYPE_JSON	= "application/json";

	private final Gson			m_gson;

	public ApacheHttpJsonClient(final Gson gson) {
		if (gson == null) {
			throw new NullPointerException("Gson must be supplied");
		}
		m_gson = gson;
	}

	/**
	 * Converts recieved JSON into passed type
	 * 
	 * @param url address to which are we posting
	 * @param requestObject post data
	 * @param responseType type of the response we are expecting
	 * @return instance of the class
	 * @throws NetworkException
	 * @throws ExternalException
	 * @throws InternalException
	 */
	public <T> T get(final URL url, final Class<T> responseType) throws ExternalException, NetworkException,
			InternalException {
		// standard post request with json content
		HttpGet request;
		try {
			request = new HttpGet(url.toURI());
		} catch (URISyntaxException ex) {
			throw new InternalException("Illegal URI syntax: " + url, ex);
		}
		request.setHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
		request.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

		return any(responseType, request);
	}

	/**
	 * Converts recieved JSON into passed type
	 * 
	 * @param url address to which are we posting
	 * @param requestObject post data
	 * @param responseType type of the response we are expecting
	 * @return instance of the class
	 * @throws NetworkException
	 * @throws ExternalException
	 * @throws InternalException
	 */
	public <T> T post(final String url, final Object requestObject, final Class<T> responseType)
			throws ExternalException, NetworkException, InternalException {
		// standard post request with json content
		HttpPost request = new HttpPost(url);
		request.setHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON);
		request.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

		// converting post data to json
		String json = m_gson.toJson(requestObject);
		try {
			StringEntity requestEntity = new StringEntity(json);
			request.setEntity(requestEntity);
		} catch (UnsupportedEncodingException ex) {
			throw new InternalException("Cannot convert request to JSON", ex);
		}

		return any(responseType, request);
	}

	protected <T> T any(final Class<T> responseType, final HttpUriRequest request) throws ExternalException,
			NetworkException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting (%s): '%s'", responseType, request.getURI());
		}
		// executing request
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android/com.twister.cineworld");
		HttpClientParams.setRedirecting(httpClient.getParams(), true); // allow redirects for GET and HEAD

		String json = null;
		InputStream is = null;
		try {
			HttpResponse httpResponse = httpClient.execute(request);
			HttpEntity entity = httpResponse.getEntity();

			checkResponseThrow(httpResponse, entity);

			is = entity.getContent();
			Reader reader = new InputStreamReader(is, IOTools.getEncoding(entity));
			json = IOTools.readAll(reader);

			T object = m_gson.fromJson(json, responseType);
			return object;
		} catch (JsonParseException ex) {
			// FIXME this is a generic class, should be no reference to Cineworld
			throw new ExternalException(ExternalException.System.CINEWORLD,
					"Could not parse JSON response:\n %s", ex, json);
		} catch (IOException ex) {
			throw new NetworkException("Could not get response", ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					LOG.warn("Could not close InputStream", ex);
				}
			}
			httpClient.close();
		}
	}

	/**
	 * Check if everything is as it should be
	 * 
	 * @param httpResponse
	 * @param entity
	 * @throws ExternalException
	 * @throws NetworkException
	 */
	private void checkResponseThrow(final HttpResponse httpResponse, final HttpEntity entity) throws ExternalException,
			NetworkException {
		StatusLine status = httpResponse.getStatusLine();
		int statusCode = status.getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new NetworkException("HTTP status code is not OK: %d (%s)", statusCode, status.getReasonPhrase());
		}

		if (entity == null) {
			// FIXME this is a generic class, should be no reference to Cineworld
			throw new ExternalException(ExternalException.System.CINEWORLD, "Empty response");
		}
	}
}
