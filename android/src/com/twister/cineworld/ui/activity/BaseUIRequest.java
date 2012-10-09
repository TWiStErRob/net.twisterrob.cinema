package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;

import com.twister.cineworld.exception.ApplicationException;

public abstract class BaseUIRequest<UIItem> implements UIRequestExtras {
	private final Intent	m_intent;

	public BaseUIRequest(final Intent intent) {
		m_intent = intent;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getExtra(final String extraKey) {
		T result = null;
		if (m_intent.hasExtra(extraKey)) {
			Object object = m_intent.getExtras().get(extraKey);
			result = (T) object;
		}
		return result;
	}

	abstract String getTitle(Resources resources);

	abstract List<UIItem> getList() throws ApplicationException;
}
