package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldEvent;

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
