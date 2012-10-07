package com.twister.cineworld.model.json.request;

import java.net.URL;

import com.twister.cineworld.model.json.data.CineworldCategory;
import com.twister.cineworld.model.json.response.CategoriesResponse;

/**
 * <p>
 * This query returns a list of film categories used by cineworld. Categories are used to conveniently group films
 * together. The API will only return 'simple' categories, such as 'Movies for Juniors', but not dynamically calculated
 * categories, such as 'Now Showing'.
 * </p>
 * <p>
 * Example request:<br>
 * <code>http://www.cineworld.com/api/categories?key=key</code>
 * </p>
 * 
 * @author papp.robert.s
 */
public class CategoriesRequest extends BaseListRequest<CineworldCategory> {
	/**
	 * Relative URI to the base API URL.
	 */
	private static final String	REQUEST_URI		= "categories";
	/**
	 * String representation of request type.
	 */
	private static final String	REQUEST_TYPE	= "categories";

	/**
	 * Creates an empty request, this request has no filtering parameters.
	 */
	public CategoriesRequest() {
		super(null, null);
	}

	/**
	 * Creates an empty request, this request has no filtering parameters.
	 * 
	 * @param key (Required) Your developer API key.
	 */
	public CategoriesRequest(final String key) {
		super(key, null, null);
	}

	/**
	 * <p>
	 * Example request:<br>
	 * <code>http://www.cineworld.com/api/categories?key=key</code>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public URL getURL() {
		return BaseListRequest.makeUrl(REQUEST_URI);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see CategoriesResponse
	 */
	@Override
	public Class<CategoriesResponse> getResponseClass() {
		return CategoriesResponse.class;
	}
}
