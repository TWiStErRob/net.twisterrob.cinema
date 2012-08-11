package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldEvent;

public class EventsResponse extends BaseResponse<CineworldEvent> {
	@SerializedName("events")
	private List<CineworldEvent>	m_events;

	public List<CineworldEvent> getEvents() {
		return Collections.unmodifiableList(m_events);
	}

	@Override
	public List<CineworldEvent> getList() {
		return getEvents();
	}
}
