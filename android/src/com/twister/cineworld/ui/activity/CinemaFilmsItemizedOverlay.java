package com.twister.cineworld.ui.activity;

import java.util.*;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;
import com.twister.cineworld.R;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.tools.CollectionTools;

public class CinemaFilmsItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private static final int				POSTER_HEIGHT	= 150;
	private static final int				POSTER_WIDTH	= 100;
	private final ArrayList<Film>			m_items			= new ArrayList<Film>();
	private final ArrayList<OverlayItem>	m_itemCache		= new ArrayList<OverlayItem>();
	private Cinema							m_cinema;
	private MapView							m_map;

	public CinemaFilmsItemizedOverlay(final Resources resources, final MapView map, final Cinema cinema) {
		super(CinemaFilmsItemizedOverlay.getDefaultMarker(resources));
		m_map = map;
		m_cinema = cinema;
	}

	private static Drawable getDefaultMarker(final Resources resources) {
		Drawable marker = resources.getDrawable(R.drawable.cineworld_logo);
		marker.setBounds(0, -CinemaFilmsItemizedOverlay.POSTER_HEIGHT, CinemaFilmsItemizedOverlay.POSTER_WIDTH, 0);
		return marker;
	}

	@Override
	protected OverlayItem createItem(final int i) {
		CollectionTools.ensureIndexValid(m_itemCache, i);
		OverlayItem item = m_itemCache.get(i);
		if (item == null) {
			final Film film = m_items.get(i);
			item = new OverlayItem(getLocation(i), film.getTitle(), String.valueOf(film.getEdi())) {
				@Override
				public Drawable getMarker(final int stateBitset) {
					Drawable marker = film.getPoster();
					if (marker != null) {
						marker.setBounds(0, -POSTER_HEIGHT, POSTER_WIDTH, 0);
					} else {
						marker = super.getMarker(stateBitset);
					}
					return marker;
				}
			};
		}
		m_itemCache.set(i, item);
		return item;
	}

	@Override
	public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
		GeoPoint center = m_cinema.getLocation().toGeoPoint();
		Projection projection = m_map.getProjection();
		Point centerPoint = projection.toPixels(center, null);
		Point filmPoint = new Point();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		if (shadow) {
			paint.setAlpha(80);
			paint.setStrokeWidth(1.5f);
		} else {
			paint.setAlpha(192);
			paint.setStrokeWidth(2.5f);
		}
		int offsetX = shadow? 4 : 0;
		int offsetY = shadow? -3 : 0;
		for (int i = 0; i < size(); ++i) {
			OverlayItem item = createItem(i);
			GeoPoint film = item.getPoint();
			projection.toPixels(film, filmPoint);
			filmPoint.offset(offsetX, offsetY);
			canvas.drawLine(centerPoint.x, centerPoint.y, filmPoint.x, filmPoint.y, paint);
		}
		super.draw(canvas, mapView, shadow);
	}

	private GeoPoint getLocation(final int i) {
		GeoPoint center = m_cinema.getLocation().toGeoPoint();
		int count = size();
		int pos = i;
		double degree = 360 / (count);
		degree *= pos;
		int radius = Math.min(m_map.getWidth(), m_map.getHeight()) / 3;
		double x = radius * Math.cos(Math.toRadians(degree));
		double y = radius * Math.sin(Math.toRadians(degree));
		Point centerPoint = m_map.getProjection().toPixels(center, null);
		centerPoint.offset((int) x, (int) y);
		return m_map.getProjection().fromPixels(centerPoint.x, centerPoint.y);
	}

	@Override
	public int size() {
		return m_items.size();
	}

	public void add(final Film film) {
		m_items.add(film);
		populate();
	}

	public void addAll(final Collection<Film> films) {
		m_items.addAll(films);
		populate();
	}

}
