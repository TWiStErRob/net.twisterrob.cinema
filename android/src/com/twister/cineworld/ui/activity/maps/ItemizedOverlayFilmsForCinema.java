package com.twister.cineworld.ui.activity.maps;

import java.util.*;

import android.graphics.*;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.tools.CollectionTools;

public class ItemizedOverlayFilmsForCinema extends ItemizedOverlay<OverlayItem> {
	private final ArrayList<CineworldFilm>	m_items		= new ArrayList<CineworldFilm>();
	private final ArrayList<OverlayItem>	m_itemCache	= new ArrayList<OverlayItem>();
	private CineworldCinema					m_cinema;
	private MapView							m_map;

	public ItemizedOverlayFilmsForCinema(final MapView map, final CineworldCinema cinema,
			final Drawable defaultMarker) {
		super(defaultMarker);
		m_map = map;
		m_cinema = cinema;
	}

	@Override
	protected OverlayItem createItem(final int i) {
		CollectionTools.ensureIndexValid(m_itemCache, i);
		OverlayItem item = m_itemCache.get(i);
		if (item == null) {
			final CineworldFilm film = m_items.get(i);
			item = new OverlayItem(getLocation(i), film.getTitle(), String.valueOf(film.getEdi())) {
				@Override
				public Drawable getMarker(final int stateBitset) {
					Drawable marker = film.getPoster();
					if (marker != null) {
						int markerWidth = marker.getIntrinsicWidth() * 2;
						int markerHeight = marker.getIntrinsicHeight() * 2;
						marker.setBounds(0, -markerHeight, markerWidth, 0);
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
		GeoPoint center = m_cinema.getLocation();
		Projection projection = m_map.getProjection();
		Point centerPoint = projection.toPixels(center, null);
		Point filmPoint = new Point();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		if (shadow) {
			paint.setAlpha(96);
			paint.setStrokeWidth(2f);
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
		GeoPoint center = m_cinema.getLocation();
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

	public void add(final CineworldFilm film) {
		m_items.add(film);
		populate();
	}

	public void addAll(final Collection<CineworldFilm> films) {
		m_items.addAll(films);
		populate();
	}

}
