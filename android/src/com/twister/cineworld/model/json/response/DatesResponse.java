package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldDate;

public class DatesResponse extends BaseListResponse<CineworldDate> {
	@SerializedName("dates")
	private List<CineworldDate>	m_dates;

	public List<CineworldDate> getDates() {
		return m_dates;
	}

	public void setDates(final List<CineworldDate> dates) {
		m_dates = dates;
	}

	@Override
	public List<CineworldDate> getList() {
		return Collections.unmodifiableList(getDates());
	}
}
