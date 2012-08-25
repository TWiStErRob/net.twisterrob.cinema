package com.twister.cineworld.ui.model;

import android.content.Intent;

public final class MainMenuItem {
	private String	m_title;
	private int		m_icon;
	private Intent	m_intent;

	public MainMenuItem() {
	}

	public MainMenuItem(final String title, final int icon, final Intent intent) {
		m_title = title;
		m_icon = icon;
		m_intent = intent;
	}

	public int getIcon() {
		return m_icon;
	}

	public Intent getIntent() {
		return m_intent;
	}

	public String getTitle() {
		return m_title;
	}
}
