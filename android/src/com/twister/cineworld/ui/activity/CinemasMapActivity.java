package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.maps.*;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Cinema;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasMapActivity extends BaseListMapActivity<Cinema> implements
		OnFocusChangeListener, OnItemSelectedListener {
	private MapView					m_map;
	private MyLocationOverlay		m_location;
	private CinemasItemizedOverlay	m_overlay;
	private CinemasUIRequest		m_request;

	public CinemasMapActivity() {
		super(R.layout.activity_cinemas_map, R.menu.context_item_cinema);
		super.setAutoLoad(false);
		super.setOptionsMenu(R.menu.activity_cinemas_map);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new CinemasUIRequest(getIntent());
		setTitle(m_request.getTitle(getResources()));

		m_map = (MapView) findViewById(R.id.mapview);
		m_map.displayZoomControls(true);
		m_map.setBuiltInZoomControls(true);

		GeoPoint center = new GeoPoint(54227511, -4538933);
		m_map.getController().setZoom(7);
		m_map.getController().setCenter(center);

		Drawable marker = getResources().getDrawable(R.drawable.cineworld_logo);
		int markerWidth = marker.getIntrinsicWidth() / 2;
		int markerHeight = marker.getIntrinsicHeight() / 2;
		marker.setBounds(0, -markerHeight, markerWidth, 0);
		Drawable marker2 = getResources().getDrawable(R.drawable.cineworld_logo);
		int markerWidth2 = marker2.getIntrinsicWidth();
		int markerHeight2 = marker2.getIntrinsicHeight();
		marker2.setBounds(0, -markerHeight2, markerWidth2, 0);
		m_overlay = new CinemasItemizedOverlay(this, m_map, marker, marker2);
		m_map.getOverlays().add(m_overlay);
		m_overlay.setOnFocusChangeListener(this);

		m_location = new MyLocationOverlay(this, m_map);
		m_location.runOnFirstFix(new Runnable() {
			public void run() {
				Location lastFix = m_location.getLastFix();
				final GeoPoint newCenter = new GeoPoint((int) (lastFix.getLatitude() * 1e6), (int) (lastFix
						.getLongitude() * 1e6));
				m_map.post(new Runnable() {
					public void run() {
						m_map.getController().setZoom(11);
						m_map.getController().setCenter(newCenter);
					}
				});
			}
		});
		m_map.getOverlays().add(m_location);
		m_map.postInvalidate();

		getSpinner().setOnItemSelectedListener(this);

		startLoad();
	}

	@Override
	protected void onResume() {
		super.onResume();
		m_location.enableMyLocation();
		m_location.enableCompass();
	}

	@Override
	protected void onPause() {
		m_location.disableMyLocation();
		m_location.disableCompass();
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuitem_cinemas_asList:
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtras(getIntent());
				this.startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Cinema item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Cinema item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_cinema_details:
				Intent intent = new Intent(getApplicationContext(), CinemaActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_CINEMA, item);
				this.startActivity(intent);
				return true;
			default:
				return super.onContextItemSelected(menu, item);
		}
	}

	@Override
	protected ListAdapter createAdapter(final List<Cinema> result) {
		return new CinemaAdapter(this, result);
	}

	@Override
	public void update(final List<Cinema> result) {
		super.update(result);
		for (Cinema cinema : result) {
			if (cinema.getLocation() != null) {
				m_overlay.addItem(cinema);
			}
		}
	}

	boolean	skipEvent	= false;

	public void onFocusChanged(@SuppressWarnings("rawtypes") final ItemizedOverlay overlay, final OverlayItem newFocus) {
		if (skipEvent) {
			skipEvent = false;
			return;
		}
		CinemaAdapter adapter = (CinemaAdapter) getSpinner().getAdapter();
		skipEvent = true;
		if (newFocus != null) {
			int i = 0;
			for (Cinema cinema : adapter.getItems()) {
				if (cinema.getLocation().near(newFocus.getPoint())) {
					break;
				}
				i++;
			}
			getSpinner().setSelection(i);
		} else {
			m_overlay.onTap(-1);
			getSpinner().setSelection(0); // TODO
		}
		m_map.postInvalidate();
	}

	public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (skipEvent) {
			skipEvent = false;
			return;
		}
		OverlayItem item = position < m_overlay.size()? m_overlay.getItem(position) : null;
		m_overlay.setFocus(item);
	}

	public void onNothingSelected(final AdapterView<?> parent) {
		m_overlay.setFocus(null);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected List<Cinema> loadList() throws ApplicationException {
		return m_request.getList();
	}

}
