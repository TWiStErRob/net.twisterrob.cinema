package com.twister.cineworld.model.json.request;

import java.net.URL;

import com.twister.cineworld.model.json.data.CineworldEvent;
import com.twister.cineworld.model.json.response.EventsResponse;

/**
 * <p>
 * This query returns a list of the current events being run by Cineworld. For example the Edinburgh Film Festival will
 * appear as an event, and all films being shown at the event could be retrieved by using the event parameter.
 * </p>
 * <p>
 * Example request:<br>
 * <code>http://www.cineworld.com/api/events?key=key</code>
 * </p>
 * 
 * @author papp.robert.s
 */
public class EventsRequest extends BaseListRequest<CineworldEvent> {
	/**
	 * Relative URI to the base API URL.
	 */
	private static final String	REQUEST_URI		= "events";
	/**
	 * String representation of request type.
	 */
	private static final String	REQUEST_TYPE	= "events";

	/**
	 * Creates an empty request, this request has no filtering parameters.
	 */
	public EventsRequest() {
		super(null, null);
	}

	/**
	 * Creates an empty request, this request has no filtering parameters.
	 * 
	 * @param key (Required) Your developer API key.
	 */
	public EventsRequest(final String key) {
		super(key, null, null);
	}

	/**
	 * <p>
	 * Example request:<br>
	 * <code>http://www.cineworld.com/api/quickbook/films?key=key&cinema=23&date=20100801&date=20100802</code>
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
	 * @see EventsResponse
	 */
	@Override
	public Class<EventsResponse> getResponseClass() {
		return EventsResponse.class;
	}
}
