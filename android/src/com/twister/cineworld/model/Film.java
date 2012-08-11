package com.twister.cineworld.model;

import com.twister.cineworld.model.json.data.CineworldFilm;

public class Film extends FilmBase {
	private CineworldFilm	m_2d;
	private CineworldFilm	m_3d;
	private CineworldFilm	m_imax2d;
	private CineworldFilm	m_imax3d;

	public void set2D(final CineworldFilm normal2D) {
		m_2d = normal2D;
	}

	public void set3D(final CineworldFilm normal3D) {
		m_3d = normal3D;
	}

	public void setIMax2D(final CineworldFilm iMax2D) {
		m_imax2d = iMax2D;
	}

	public void setIMax3D(final CineworldFilm iMax3D) {
		m_imax3d = iMax3D;
	}

	@Override
	public boolean has2D() {
		return m_2d != null;
	}

	@Override
	public boolean has3D() {
		return m_3d != null;
	}

	@Override
	public boolean hasIMax2D() {
		return m_imax2d != null;
	}

	@Override
	public boolean hasIMax3D() {
		return m_imax3d != null;
	}

	public void add(final CineworldFilm cineFilm) {
		if (cineFilm.isIMax()) {
			if (cineFilm.is3D()) {
				m_imax3d = cineFilm;
			} else {
				m_imax2d = cineFilm;
			}
		} else {
			if (cineFilm.is3D()) {
				m_3d = cineFilm;
			} else {
				m_2d = cineFilm;
			}
		}
	}
}
