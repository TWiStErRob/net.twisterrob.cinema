package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;

final class CinemasUIRequest extends BaseUIRequest<Cinema> {
	private final Film			m_film;
	private final Distributor	m_distributor;
	private final Event			m_event;
	private final Category		m_category;

	CinemasUIRequest(final Intent intent) {
		super(intent);
		m_film = getExtra(EXTRA_FILM);
		m_distributor = getExtra(EXTRA_DISTRIBUTOR);
		m_event = getExtra(EXTRA_EVENT);
		m_category = getExtra(EXTRA_CATEGORY);
	}

	@Override
	public String getTitle(final Resources resources) {
		if (m_film != null) {
			return resources.getString(R.string.title_activity_cinemas_forFilm, m_film.getTitle());
		} else if (m_distributor != null) {
			return resources.getString(R.string.title_activity_cinemas_forDistributor, m_distributor.getName());
		} else if (m_event != null) {
			return resources.getString(R.string.title_activity_cinemas_forEvent, m_event.getName());
		} else if (m_category != null) {
			return resources.getString(R.string.title_activity_cinemas_forCategory, m_category.getName());
		} else {
			return resources.getString(R.string.title_activity_cinemas_all, m_film);
		}
	}

	@Override
	public List<Cinema> getList() throws ApplicationException {
		if (m_film != null) {
			return App.getInstance().getCineworldAccessor().getCinemasForFilm(m_film.getEdi());
		} else if (m_distributor != null) {
			return App.getInstance().getCineworldAccessor().getCinemasForDistributor(m_distributor.getId());
		} else if (m_event != null) {
			return App.getInstance().getCineworldAccessor().getCinemasForEvent(m_event.getCode());
		} else if (m_category != null) {
			return App.getInstance().getCineworldAccessor().getCinemasForCategory(m_category.getCode());
		} else {
			return App.getInstance().getCineworldAccessor().getAllCinemas();
		}
	}
}