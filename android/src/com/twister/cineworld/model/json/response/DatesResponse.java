package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldDate;

public class DatesResponse extends BaseResponse<CineworldDate> {
	@SerializedName("dates")
	private List<CineworldDate>	m_dates;

	public List<CineworldDate> getDates() {
		return Collections.unmodifiableList(m_dates);
	}

	@Override
	public List<CineworldDate> getList() {
		return getDates();
	}
}
