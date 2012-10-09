package com.twister.cineworld.ui.activity;

import java.util.*;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Cinema;

final class CinemaUIRequest extends BaseUIRequest<Cinema> {
	private final Cinema	m_cinema;

	CinemaUIRequest(final Intent intent) {
		super(intent);
		m_cinema = getExtra(EXTRA_CINEMA);
	}

	@Override
	public String getTitle(final Resources resources) {
		return resources.getString(R.string.title_activity_cinema, m_cinema);
	}

	@Override
	public List<Cinema> getList() throws ApplicationException {
		return Collections.singletonList(m_cinema);
	}

	public Cinema getCinema() {
		return m_cinema;
	}
}
