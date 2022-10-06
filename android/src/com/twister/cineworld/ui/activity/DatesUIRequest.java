package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;

final class DatesUIRequest extends BaseUIRequest<Date> {
	private final Film		m_film;
	private final Cinema	m_cinema;

	DatesUIRequest(final Intent intent) {
		super(intent);
		m_film = getExtra(EXTRA_FILM);
		m_cinema = getExtra(EXTRA_CINEMA);
	}

	@Override
	public String getTitle(final Resources resources) {
		if (m_cinema != null && m_film != null) {
			return resources.getString(R.string.title_activity_dates_forCinemaFilm,
					m_cinema.getName(), m_film.getTitle());
		} else if (m_film != null) {
			return resources.getString(R.string.title_activity_dates_forFilm, m_film.getTitle());
		} else if (m_cinema != null) {
			return resources.getString(R.string.title_activity_dates_forCinema, m_cinema.getName());
		} else {
			return resources.getString(R.string.title_activity_dates_all, "");
		}
	}

	@Override
	public List<Date> getList() throws ApplicationException {
		if (m_cinema != null && m_film != null) {
			return App.getInstance().getCineworldAccessor().getDatesForFilmAtCinema(m_film.getEdi(), m_cinema.getId());
		} else if (m_film != null) {
			return App.getInstance().getCineworldAccessor().getDatesForFilm(m_film.getEdi());
		} else if (m_cinema != null) {
			return App.getInstance().getCineworldAccessor().getDatesForCinema(m_cinema.getId());
		} else {
			return App.getInstance().getCineworldAccessor().getAllDates();
		}
	}

	public Cinema getCinema() {
		return m_cinema;
	}

	public Film getFilm() {
		return m_film;
	}

}
