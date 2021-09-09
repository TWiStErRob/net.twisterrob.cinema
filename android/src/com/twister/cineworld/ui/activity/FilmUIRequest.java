package com.twister.cineworld.ui.activity;

import java.util.*;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Film;

final class FilmUIRequest extends BaseUIRequest<Film> {
	private final Film	m_film;

	FilmUIRequest(final Intent intent) {
		super(intent);
		m_film = getExtra(EXTRA_FILM);
	}

	@Override
	public String getTitle(final Resources resources) {
		return resources.getString(R.string.title_activity_film, m_film.getTitle());
	}

	@Override
	public List<Film> getList() throws ApplicationException {
		return Collections.singletonList(m_film);
	}

	public Film getFilm() {
		return m_film;
	}
}
