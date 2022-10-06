package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldEvent;

/**
 * <p>
 * Use this to retrieve an unordered array of film events, returned in JSON format. An example of what will be returned
 * is below:
 * 
 * <pre>
 * {"events":[{
 *     "code":"metopera",
 *     "name":"MET Opera"
 * }]}
 * </pre>
 * 
 * </p>
 * <p>
 * If any errors should occur they will be returned as an array:
 * 
 * <pre>
 * {"errors":["valid key not supplied"]}
 * </pre>
 * 
 * </p>
 * 
 * @author papp.robert.s
 * @see CineworldEvent
 */
public class EventsResponse extends BaseListResponse<CineworldEvent> {
	@SerializedName("events")
	private List<CineworldEvent>	m_events;

	public List<CineworldEvent> getEvents() {
		return m_events;
	}

	public void setEvents(final List<CineworldEvent> events) {
		m_events = events;
	}

	@Override
	public List<CineworldEvent> getList() {
		return Collections.unmodifiableList(getEvents());
	}
}
