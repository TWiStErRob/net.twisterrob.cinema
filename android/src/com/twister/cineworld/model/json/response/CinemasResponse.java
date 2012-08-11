package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldCinema;

public class CinemasResponse extends BaseResponse<CineworldCinema> {
	@SerializedName("cinemas")
	private List<CineworldCinema>	m_cinemas;

	public List<CineworldCinema> getCinemas() {
		return Collections.unmodifiableList(m_cinemas);
	}

	@Override
	public List<CineworldCinema> getList() {
		return getCinemas();
	}
}
