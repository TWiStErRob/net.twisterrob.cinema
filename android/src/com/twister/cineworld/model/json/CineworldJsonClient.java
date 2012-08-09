package com.twister.cineworld.model.json;

import java.io.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;

public class CineworldJsonClient {
	private static final String	CONTENT_TYPE_JSON	= "application/json";
	private final Gson			m_gson				= new Gson();

	/**
	 * Converts recieved JSON into passed type
	 * 
	 * @param url address to which are we posting
	 * @param requestObject post data
	 * @param responseType type of the response we are expecting
	 * @return instance of the class
	 * @throws Exception use more precise exceptions
	 */
	public <T> T get(final String url, final Class<T> responseType) throws IOException {
		// standard post request with json content
		HttpGet request = new HttpGet(url);
		request.setHeader("Accept", CineworldJsonClient.CONTENT_TYPE_JSON);
		request.setHeader("Content-type", CineworldJsonClient.CONTENT_TYPE_JSON);

		return any(responseType, request);
	}

	/**
	 * Converts recieved JSON into passed type
	 * 
	 * @param url address to which are we posting
	 * @param requestObject post data
	 * @param responseType type of the response we are expecting
	 * @return instance of the class
	 * @throws Exception use more precise exceptions
	 */
	public <T> T post(final String url, final Object requestObject, final Class<T> responseType) throws IOException {
		// standard post request with json content
		HttpPost request = new HttpPost(url);
		request.setHeader("Accept", CineworldJsonClient.CONTENT_TYPE_JSON);
		request.setHeader("Content-type", CineworldJsonClient.CONTENT_TYPE_JSON);

		// converting post data to json
		String json = m_gson.toJson(requestObject);
		StringEntity requestEntity = new StringEntity(json);
		request.setEntity(requestEntity);

		return any(responseType, request);
	}

	public <T> T any(final Class<T> responseType, final HttpUriRequest request) throws IOException {
		Log.i("JSON", "Getting (" + responseType + "): '" + request.getURI() + "'");
		// executing request
		HttpClient httpClient = AndroidHttpClient.newInstance("Android/com.twister.cineworld");
		HttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();

		checkResponseThrow(httpResponse, entity);

		InputStream content = entity.getContent();
		Reader reader = new InputStreamReader(content);

		T object = m_gson.fromJson(reader, responseType);
		return object;
	}

	// check if everything is as it should be
	private void checkResponseThrow(final HttpResponse httpResponse, final HttpEntity entity) throws IOException {
		StatusLine status = httpResponse.getStatusLine();
		int statusCode = status
				.getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new HttpResponseException(status.getStatusCode(), status.getReasonPhrase());
		}

		if (entity == null) {
			throw new MalformedJsonException("Got empty response…");
		}

		Header contentType = entity.getContentType();
		if (contentType == null || !contentType.getValue().startsWith(CineworldJsonClient.CONTENT_TYPE_JSON)) {
			String message = "Received invalid content type: " + contentType.getValue() + ", expected: " + CineworldJsonClient.CONTENT_TYPE_JSON;
			Log.w("JSON", message);
			// throw new HttpResponseException(status.getStatusCode(), message);
		}
	}
}
