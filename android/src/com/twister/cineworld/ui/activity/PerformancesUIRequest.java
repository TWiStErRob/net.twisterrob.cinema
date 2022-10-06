package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.model.generic.*;

final class PerformancesUIRequest extends BaseUIRequest<Performance> {
	private final Cinema	m_cinema;
	private final Film		m_film;
	private final Date		m_date;

	PerformancesUIRequest(final Intent intent) {
		super(intent);
		m_cinema = getExtra(EXTRA_CINEMA);
		m_film = getExtra(EXTRA_FILM);
		m_date = getExtra(EXTRA_DATE);
	}

	@Override
	public String getTitle(final Resources resources) {
		if (m_cinema != null && m_film != null && m_date != null)
			return resources.getString(R.string.title_activity_performances_specific, m_cinema.getName(),
					m_film.getTitle(), m_date.getCalendar());
		else
			return resources.getString(R.string.title_activity_error, PerformancesActivity.class.getSimpleName());
	}

	@Override
	public List<Performance> getList() throws ApplicationException {
		List<Performance> list = null;
		if (m_cinema != null && m_film != null && m_date != null) {
			list = App.getInstance().getCineworldAccessor()
					.getPeformances(m_cinema.getId(), m_film.getEdi(), m_date.getDate());
		} else if (m_cinema == null && m_film == null && m_date == null) {
			list = App.getInstance().getCineworldAccessor().getPeformances(66, 62278, "20130427");
		} else
			throw new InternalException(
					"Cannot retrieve performances, not enough arguments: cinema=%s, film=%s, date=%s",
					m_cinema, m_film, m_date);
		return list;
	}
}
