package com.twister.cineworld.model.generic;

public class Movie extends MovieBase {
	private Film	m_2D;
	private Film	m_3D;
	private Film	m_imax2D;
	private Film	m_imax3D;

	public void set2D(final Film normal2D) {
		m_2D = normal2D;
	}

	public void set3D(final Film normal3D) {
		m_3D = normal3D;
	}

	public void setIMax2D(final Film iMax2D) {
		m_imax2D = iMax2D;
	}

	public void setIMax3D(final Film iMax3D) {
		m_imax3D = iMax3D;
	}

	@Override
	public boolean has2D() {
		return m_2D != null;
	}

	@Override
	public boolean has3D() {
		return m_3D != null;
	}

	@Override
	public boolean hasIMax2D() {
		return m_imax2D != null;
	}

	@Override
	public boolean hasIMax3D() {
		return m_imax3D != null;
	}

	public Film get2D() {
		return m_2D;
	}

	public Film get3D() {
		return m_3D;
	}

	public Film getIMax2D() {
		return m_imax2D;
	}

	public Film getIMax3D() {
		return m_imax3D;
	}

	public void add(final Film cineFilm) {
		if (cineFilm.isIMax()) {
			if (cineFilm.is3D()) {
				m_imax3D = cineFilm;
			} else {
				m_imax2D = cineFilm;
			}
		} else {
			if (cineFilm.is3D()) {
				m_3D = cineFilm;
			} else {
				m_2D = cineFilm;
			}
		}
	}
}
