package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;

final class DatesUIRequest extends BaseUIRequest<Date> {
	private final Film	m_film;

	DatesUIRequest(final Intent intent) {
		super(intent);
		m_film = getExtra(EXTRA_FILM);
	}

	@Override
	public String getTitle(final Resources resources) {
		if (m_film != null) {
			return resources.getString(R.string.title_activity_dates_forFilm, m_film.getTitle());
		} else {
			return resources.getString(R.string.title_activity_dates_all, "");
		}
	}

	@Override
	public List<Date> getList() throws ApplicationException {
		if (m_film != null) {
			return App.getInstance().getCineworldAccessor().getDatesForFilm(m_film.getEdi());
		} else {
			return App.getInstance().getCineworldAccessor().getAllDates();
		}
	}
}
