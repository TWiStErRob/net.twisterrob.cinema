package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;

final class FilmsUIRequest extends BaseUIRequest<Film> {
	private final Cinema		m_cinema;
	private final Distributor	m_distributor;
	private final Event			m_event;
	private final Category		m_category;
	private final Date			m_date;

	FilmsUIRequest(final Intent intent) {
		super(intent);
		m_cinema = getExtra(EXTRA_CINEMA);
		m_distributor = getExtra(EXTRA_DISTRIBUTOR);
		m_event = getExtra(EXTRA_EVENT);
		m_category = getExtra(EXTRA_CATEGORY);
		m_date = getExtra(EXTRA_DATE);
	}

	@Override
	public String getTitle(final Resources resources) {
		if (m_cinema != null) {
			return resources.getString(R.string.title_activity_films_forCinema, m_cinema.getName());
		} else if (m_distributor != null) {
			return resources.getString(R.string.title_activity_films_forDistributor, m_distributor.getName());
		} else if (m_event != null) {
			return resources.getString(R.string.title_activity_films_forEvent, m_event.getName());
		} else if (m_category != null) {
			return resources.getString(R.string.title_activity_films_forCategory, m_category.getName());
		} else if (m_date != null) {
			return resources.getString(R.string.title_activity_films_forDate, m_date.getDate());
		} else {
			return resources.getString(R.string.title_activity_films_all);
		}
	}

	@Override
	public List<Film> getList() throws ApplicationException {
		List<Film> list = null;
		if (m_cinema != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForCinema(m_cinema.getId());
		} else if (m_distributor != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForDistributor(m_distributor.getId());
		} else if (m_event != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForEvent(m_event.getCode());
		} else if (m_category != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForCategory(m_category.getCode());
		} else if (m_date != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForDate(m_date.getCalendar());
		} else {
			list = App.getInstance().getCineworldAccessor().getAllFilms();
		}
		return list;
	}
}
