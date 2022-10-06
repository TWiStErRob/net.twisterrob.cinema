package com.twister.cineworld.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.Cinema;
import com.twister.cineworld.tools.CollectionTools;
import com.twister.cineworld.ui.CineworldExecutor;

public class CinemasItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private static final Log				LOG			= LogFactory.getLog(Tag.UI);

	private final ArrayList<Cinema>			m_items		= new ArrayList<Cinema>();
	private final Drawable					m_markerSelected;
	private final MapView					m_map;
	private final ArrayList<OverlayItem>	m_itemCache	= new ArrayList<OverlayItem>();
	private final Activity					m_activity;

	public CinemasItemizedOverlay(final Activity activity, final MapView map,
			final Drawable marker, final Drawable markerSelected) {
		super(marker);
		m_activity = activity;
		m_map = map;
		m_markerSelected = markerSelected;
		populate();
	}

	public void addItem(final Cinema cinema) {
		m_items.add(cinema);
		populate();
	}

	@Override
	protected OverlayItem createItem(final int i) {
		CollectionTools.ensureIndexValid(m_itemCache, i);
		OverlayItem item = m_itemCache.get(i);
		if (item == null) {
			Cinema cinema = m_items.get(i);
			item = new OverlayItem(cinema.getLocation().toGeoPoint(), cinema.getName(),
					String.format("%s, %s", cinema.getAddress(), cinema.getPostcode())) {
				@Override
				public Drawable getMarker(final int stateBitset) {
					return stateBitset != 0? m_markerSelected : super.getMarker(stateBitset);
				}
			};
		}
		m_itemCache.set(i, item);
		return item;
	}

	@Override
	public int size() {
		return m_items.size();
	}

	@Override
	protected boolean onTap(final int index) {
		CollectionTools.remove(m_map.getOverlays(), CinemaFilmsItemizedOverlay.class);
		if (index < 0 || size() <= index) {
			return false;
		}
		OverlayItem item = getItem(index);
		final Cinema cinema = m_items.get(index);
		item.setMarker(null);
		CineworldExecutor.execute(new CinemaFilmsItemizedOverlayLoader(m_activity, m_map, cinema));
		m_map.getController().animateTo(cinema.getLocation().toGeoPoint());
		// m_map.removeView(m_popup);
		// View popUp = getLayoutInflater().inflate(R.layout.map_popup, map, false);
		// MapView.LayoutParams mapParams = new MapView.LayoutParams(
		// ViewGroup.LayoutParams.WRAP_CONTENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT,
		// item.getPoint(),
		// 0,
		// 0,
		// MapView.LayoutParams.BOTTOM_CENTER);
		// m_map.addView(popUp, mapParams);
		return true;
	}
}
