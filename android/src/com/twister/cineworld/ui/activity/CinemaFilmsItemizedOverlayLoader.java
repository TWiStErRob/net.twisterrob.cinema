package com.twister.cineworld.ui.activity;

import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.*;

import com.google.android.maps.MapView;
import com.twister.cineworld.App;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.json.TimeSpan;
import com.twister.cineworld.tools.cache.Cache;
import com.twister.cineworld.ui.CineworldGUITask;

public class CinemaFilmsItemizedOverlayLoader extends CineworldGUITask<List<Film>> {
	private static final Log					LOG	= LogFactory.getLog(Tag.UI);

	private final Cinema						m_cinema;
	private final MapView						m_map;
	private final CinemaFilmsItemizedOverlay	overlay;

	public CinemaFilmsItemizedOverlayLoader(final Activity activity, final MapView map, final Cinema cinema) {
		super(activity);
		m_map = map;
		m_cinema = cinema;
		overlay = new CinemaFilmsItemizedOverlay(activity.getResources(), m_map, m_cinema);
	}

	@Override
	protected List<Film> work() throws ApplicationException {
		Accessor accessor = App.getInstance().getCineworldAccessor();
		List<Film> films = accessor.getFilmsForCinema(m_cinema.getId(), TimeSpan.Tomorrow);
		for (Film film : films) {
			try {
				Cache<URL, Bitmap> cache = App.getInstance().getPosterCache();
				Bitmap bitmap = cache.get(film.getPosterUrl());
				film.setPoster(CinemaFilmsItemizedOverlayLoader.createDrawable(bitmap));
			} catch (ApplicationException ex) {
				LOG.warn("Could not download film poster: %s", ex, film.getPosterUrl());
			}
		}
		return films;
	}

	private static Drawable createDrawable(final Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		return new BitmapDrawable(bitmap);
	}

	@Override
	protected void present(final List<Film> result) {
		overlay.addAll(result);
		m_map.getOverlays().add(overlay);
	}

	@Override
	protected void exception(final ApplicationException e) {
		// TODO is there anything to do here?
	}

	@Override
	protected String whatAmIDoing() {
		return "load posters";
	}
}
