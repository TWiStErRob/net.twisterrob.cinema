package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldFilm;

public class FilmsResponse extends BaseResponse<CineworldFilm> {
	@SerializedName("films")
	private List<CineworldFilm>	m_films;

	public List<CineworldFilm> getFilms() {
		return m_films;
	}

	public void setFilms(final List<CineworldFilm> films) {
		m_films = films;
	}

	@Override
	public List<CineworldFilm> getList() {
		return Collections.unmodifiableList(getFilms());
	}
}
