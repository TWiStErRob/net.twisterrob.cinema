package com.twister.cineworld.model.json.request;

import java.net.URL;

import com.twister.cineworld.model.json.data.CineworldDistributor;
import com.twister.cineworld.model.json.response.DistributorsResponse;

/**
 * <p>
 * This query returns a list of unique distributors for the films programmed at Cineworld.
 * </p>
 * <p>
 * Example request:<br>
 * <code>http://www.cineworld.com/api/distributors?key=key</code>
 * </p>
 * 
 * @author papp.robert.s
 */
public class DistributorsRequest extends BaseListRequest<CineworldDistributor> {
	/**
	 * Relative URI to the base API URL.
	 */
	private static final String REQUEST_URI = "distributors";
	/**
	 * String representation of request type.
	 */
	private static final String REQUEST_TYPE = "distributors";

	/**
	 * Creates an empty request, this request has no filtering parameters.
	 */
	public DistributorsRequest() {
		super(null, null);
	}

	/**
	 * Creates an empty request, this request has no filtering parameters.
	 * 
	 * @param key (Required) Your developer API key.
	 */
	public DistributorsRequest(final String key) {
		super(key, null, null);
	}

	/**
	 * <p>
	 * Example request:<br>
	 * <code>http://www.cineworld.com/api/distributors?key=key</code>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override public URL getURL() {
		return makeUrl(REQUEST_URI);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public String getRequestType() {
		return REQUEST_TYPE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see DistributorsResponse
	 */
	@Override public Class<DistributorsResponse> getResponseClass() {
		return DistributorsResponse.class;
	}
}
