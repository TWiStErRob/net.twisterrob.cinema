package com.twister.cineworld.model;

import com.twister.cineworld.model.json.data.CineworldFilm;

public class Film extends FilmBase {
	private CineworldFilm	m_2D;
	private CineworldFilm	m_3D;
	private CineworldFilm	m_imax2D;
	private CineworldFilm	m_imax3D;

	public void set2D(final CineworldFilm normal2D) {
		m_2D = normal2D;
	}

	public void set3D(final CineworldFilm normal3D) {
		m_3D = normal3D;
	}

	public void setIMax2D(final CineworldFilm iMax2D) {
		m_imax2D = iMax2D;
	}

	public void setIMax3D(final CineworldFilm iMax3D) {
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

	public CineworldFilm get2D() {
		return m_2D;
	}

	public CineworldFilm get3D() {
		return m_3D;
	}

	public CineworldFilm getIMax2D() {
		return m_imax2D;
	}

	public CineworldFilm getIMax3D() {
		return m_imax3D;
	}

	public void add(final CineworldFilm cineFilm) {
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
