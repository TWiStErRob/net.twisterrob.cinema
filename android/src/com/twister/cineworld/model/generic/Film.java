package com.twister.cineworld.model.generic;

import java.net.URL;

import android.graphics.drawable.Drawable;

public class Film extends GenericBase {
	private static final long	serialVersionUID	= -3984762522613916227L;

	private int					m_edi;
	private String				m_title;
	private int					m_id;
	private String				m_classification;
	private String				m_advisory;
	private URL					m_posterUrl;
	private URL					m_stillUrl;
	private URL					m_filmUrl;
	private boolean				m_3D;
	private boolean				m_iMax;

	private volatile Drawable	m_poster;

	public int getEdi() {
		return m_edi;
	}

	public void setEdi(final int edi) {
		m_edi = edi;
	}

	public String getTitle() {
		return m_title;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	public String getClassification() {
		return m_classification;
	}

	public void setClassification(final String classification) {
		m_classification = classification;
	}

	public String getAdvisory() {
		return m_advisory;
	}

	public void setAdvisory(final String advisory) {
		m_advisory = advisory;
	}

	public URL getPosterUrl() {
		return m_posterUrl;
	}

	public void setPosterUrl(final URL posterUrl) {
		m_posterUrl = posterUrl;
	}

	public URL getStillUrl() {
		return m_stillUrl;
	}

	public void setStillUrl(final URL stillUrl) {
		m_stillUrl = stillUrl;
	}

	public URL getFilmUrl() {
		return m_filmUrl;
	}

	public void setFilmUrl(final URL filmUrl) {
		m_filmUrl = filmUrl;
	}

	public boolean is3D() {
		return m_3D;
	}

	public void set3D(final boolean is3D) {
		m_3D = is3D;
	}

	public boolean isIMax() {
		return m_iMax;
	}

	public void setIMax(final boolean iMax) {
		m_iMax = iMax;
	}

	public Drawable getPoster() {
		return m_poster;
	}

	public void setPoster(final Drawable poster) {
		m_poster = poster;
	}
}
