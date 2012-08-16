package com.twister.cineworld.ui.activity.maps;

import java.util.*;

import android.graphics.drawable.Drawable;

import com.google.android.maps.*;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem>	m_overlayItemList	= new ArrayList<OverlayItem>();
	private Drawable				m_markerSelected;

	public MyItemizedOverlay(final Drawable marker, final Drawable markerSelected) {
		super(marker);
		m_markerSelected = markerSelected;
		populate();
	}

	public void addItem(final GeoPoint p, final String title, final String snippet) {
		OverlayItem newItem = new OverlayItem(p, title, snippet) {
			@Override
			public Drawable getMarker(final int stateBitset) {
				return stateBitset != 0? m_markerSelected : super.getMarker(stateBitset);
			}
		};
		m_overlayItemList.add(newItem);
		populate();
	}

	@Override
	protected OverlayItem createItem(final int i) {
		return m_overlayItemList.get(i);
	}

	@Override
	public int size() {
		return m_overlayItemList.size();
	}

	public List<OverlayItem> getItems() {
		return Collections.unmodifiableList(m_overlayItemList);
	}

	// @Override
	// protected boolean onTap(final int index) {
	// OverlayItem item = getItem(index);
	// item.setMarker(null);
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
	// return true;
	// }
}
