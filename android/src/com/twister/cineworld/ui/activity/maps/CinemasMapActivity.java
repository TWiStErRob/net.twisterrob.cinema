package com.twister.cineworld.ui.activity.maps;

import java.io.IOException;
import java.util.*;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.maps.*;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldCinema;
import com.twister.cineworld.ui.activity.CinemaActivity;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasMapActivity extends BaseListMapActivity<CineworldCinema, CineworldCinema> implements OnFocusChangeListener, OnItemSelectedListener {
	private MapView								m_map;
	private MyLocationOverlay					m_location;
	private MyItemizedOverlay					m_overlay;

	private static final Map<String, GeoPoint>	s_locations	= new HashMap<String, GeoPoint>();
	static {
		CinemasMapActivity.s_locations.put("AB24 5EN", new GeoPoint(57150274, -2077960));
		CinemasMapActivity.s_locations.put("AB11 5RG", new GeoPoint(57143560, -2096900));
		CinemasMapActivity.s_locations.put("TN25 4BN", new GeoPoint(51161418, 871635));
		CinemasMapActivity.s_locations.put("OL7 0PG", new GeoPoint(53489036, -2111578));
		CinemasMapActivity.s_locations.put("MK41 9LW", new GeoPoint(52133311, -445862));
		CinemasMapActivity.s_locations.put("DA6 7LL", new GeoPoint(51456276, 150038));
		CinemasMapActivity.s_locations.put("B15 1DA", new GeoPoint(52474393, -1914670));
		CinemasMapActivity.s_locations.put("NE35 9PB", new GeoPoint(54946785, -1466316));
		CinemasMapActivity.s_locations.put("BL1 8TS", new GeoPoint(53597185, -2423956));
		CinemasMapActivity.s_locations.put("BD1 5LD", new GeoPoint(53791609, -1747755));
		CinemasMapActivity.s_locations.put("CM77 8YH", new GeoPoint(51869979, 570965));
		CinemasMapActivity.s_locations.put("BN2 5UF", new GeoPoint(50812637, -100692));
		CinemasMapActivity.s_locations.put("BS14 0HR", new GeoPoint(51416347, -2587139));
		CinemasMapActivity.s_locations.put("DE14 1NQ", new GeoPoint(52805594, -1632463));
		CinemasMapActivity.s_locations.put("IP33 3BA", new GeoPoint(52246199, 706604));
		CinemasMapActivity.s_locations.put("CB1 7DY", new GeoPoint(52190163, 136962));
		CinemasMapActivity.s_locations.put("CF10 2EN", new GeoPoint(51478735, -3173395));
		CinemasMapActivity.s_locations.put("WF10 4TA", new GeoPoint(53710173, -1341896));
		CinemasMapActivity.s_locations.put("SW3 5EW", new GeoPoint(51485333, -173551));
		CinemasMapActivity.s_locations.put("GL50 4EF", new GeoPoint(51902925, -2075025));
		CinemasMapActivity.s_locations.put("CH1 4QQ", new GeoPoint(53197686, -2917127));
		CinemasMapActivity.s_locations.put("S40 2ED", new GeoPoint(53227146, -1424189));
		CinemasMapActivity.s_locations.put("PO19 8EL", new GeoPoint(50830018, -785081));
		CinemasMapActivity.s_locations.put("RH10 8LR", new GeoPoint(51120235, -189645));
		CinemasMapActivity.s_locations.put("OX11 7ND", new GeoPoint(51607633, -1238501));
		CinemasMapActivity.s_locations.put("M20 5PG", new GeoPoint(53407988, -2220440));
		CinemasMapActivity.s_locations.put("DD2 4TF", new GeoPoint(56484392, -3046190));
		CinemasMapActivity.s_locations.put("BN23 6JH", new GeoPoint(50794853, 322845));
		CinemasMapActivity.s_locations.put("EH11 1AF", new GeoPoint(55941939, -3216935));
		CinemasMapActivity.s_locations.put("EN1 1YQ", new GeoPoint(51650908, -61218));
		CinemasMapActivity.s_locations.put("FK1 1LW", new GeoPoint(56003773, -3779903));
		CinemasMapActivity.s_locations.put("TW13 7LX", new GeoPoint(51443588, -406448));
		CinemasMapActivity.s_locations.put("SW10 9QR", new GeoPoint(51487151, -179428));
		CinemasMapActivity.s_locations.put("G31 4EB", new GeoPoint(55853441, -4199821));
		CinemasMapActivity.s_locations.put("G2 3AB", new GeoPoint(55864904, -4255177));
		CinemasMapActivity.s_locations.put("GL1 5SF", new GeoPoint(51857595, -2253618));
		CinemasMapActivity.s_locations.put("W6 9JT", new GeoPoint(51492525, -233306));
		CinemasMapActivity.s_locations.put("CM20 2DA", new GeoPoint(51783876, 108064));
		CinemasMapActivity.s_locations.put("CB9 0ER", new GeoPoint(52083648, 439979));
		CinemasMapActivity.s_locations.put("SW1Y 4RL", new GeoPoint(51508803, -132393));
		CinemasMapActivity.s_locations.put("HP11 2DB", new GeoPoint(51630805, -756212));
		CinemasMapActivity.s_locations.put("HU7 3DB", new GeoPoint(53792485, -352993));
		CinemasMapActivity.s_locations.put("PE29 7EG", new GeoPoint(52351409, -180699));
		CinemasMapActivity.s_locations.put("IG1 1BP", new GeoPoint(51557634, 74113));
		CinemasMapActivity.s_locations.put("IP1 1AX", new GeoPoint(52053221, 1151000));
		CinemasMapActivity.s_locations.put("PO30 2TA", new GeoPoint(50698962, -1289230));
		CinemasMapActivity.s_locations.put("JE2 4HE", new GeoPoint(54617791, -107384442));
		CinemasMapActivity.s_locations.put("WN7 4PE", new GeoPoint(53494022, -2515212));
		CinemasMapActivity.s_locations.put("L13 1EW", new GeoPoint(53408399, -2922888));
		CinemasMapActivity.s_locations.put("LL31 9XX", new GeoPoint(53282271, -3808416));
		CinemasMapActivity.s_locations.put("LU1 2NB", new GeoPoint(51881848, -417659));
		CinemasMapActivity.s_locations.put("TS1 2DY", new GeoPoint(54574656, -1226261));
		CinemasMapActivity.s_locations.put("MK9 3XS", new GeoPoint(52041427, -748697));
		CinemasMapActivity.s_locations.put("NP19 4QQ", new GeoPoint(51577570, -2943582));
		CinemasMapActivity.s_locations.put("NN5 5QJ", new GeoPoint(52235218, -936596));
		CinemasMapActivity.s_locations.put("NG1 4AA", new GeoPoint(52955403, -1149805));
		CinemasMapActivity.s_locations.put("ME2 2SS", new GeoPoint(51380031, 477083));
		CinemasMapActivity.s_locations.put("CV21 1RW", new GeoPoint(52385225, -1259181));
		CinemasMapActivity.s_locations.put("WA7 2FQ", new GeoPoint(53326183, -2699968));
		CinemasMapActivity.s_locations.put("W1D 7DH", new GeoPoint(51510953, -133011));
		CinemasMapActivity.s_locations.put("S9 2EP", new GeoPoint(53401520, -1414966));
		CinemasMapActivity.s_locations.put("SY3 7ET", new GeoPoint(52703534, -2740530));
		CinemasMapActivity.s_locations.put("B91 3GS", new GeoPoint(52412403, -1779387));
		CinemasMapActivity.s_locations.put("SO14 3TJ", new GeoPoint(50895193, -1394938));
		CinemasMapActivity.s_locations.put("WA10 1BF", new GeoPoint(53451370, -2740228));
		CinemasMapActivity.s_locations.put("NW2 6LW", new GeoPoint(51570291, -229715));
		CinemasMapActivity.s_locations.put("SG1 2UA", new GeoPoint(51899793, -208623));
		CinemasMapActivity.s_locations.put("SK1 3TA", new GeoPoint(53406906, -2160912));
		CinemasMapActivity.s_locations.put("SN5 7DN", new GeoPoint(51560797, -1831333));
		CinemasMapActivity.s_locations.put("SE10 0DX", new GeoPoint(51501570, 5531));
		CinemasMapActivity.s_locations.put("WF2 9SH", new GeoPoint(53677148, -1506104));
		CinemasMapActivity.s_locations.put("SW18 4TF", new GeoPoint(51455402, -194192));
		CinemasMapActivity.s_locations.put("E14 4AL", new GeoPoint(51507645, -23844));
		CinemasMapActivity.s_locations.put("DT4 8LY", new GeoPoint(50610237, -2455119));
		CinemasMapActivity.s_locations.put("OX28 6GW", new GeoPoint(51787148, -1486060));
		CinemasMapActivity.s_locations.put("WV11 1TZ", new GeoPoint(52596272, -2093200));
		CinemasMapActivity.s_locations.put("N22 6LU", new GeoPoint(51594544, -106456));
		CinemasMapActivity.s_locations.put("BA20 1NP", new GeoPoint(50940369, -2625528));
	}

	public CinemasMapActivity() {
		super(R.layout.activity_cinemas_map, R.menu.context_item_cinema);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		m_overlay = new MyItemizedOverlay(marker, marker2);
		m_map.getOverlays().add(m_overlay);
		m_overlay.setOnFocusChangeListener(this);

		m_location = new MyLocationOverlay(this, m_map);
		m_location.runOnFirstFix(new Runnable() {
			public void run() {
				Location lastFix = m_location.getLastFix();
				final GeoPoint newCenter = new GeoPoint((int) (lastFix.getLatitude() * 1e6), (int) (lastFix.getLongitude() * 1e6));
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		m_location.enableMyLocation();
		m_location.enableCompass();
	}

	@Override
	protected void onPause() {
		super.onPause();
		m_location.disableMyLocation();
		m_location.disableCompass();
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldCinema item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final CineworldCinema item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_cinema_details:
				Intent intent = new Intent(getApplicationContext(), CinemaActivity.class);
				intent.putExtra(CinemaActivity.EXTRA_ID, item.getId());
				this.startActivity(intent);
				return true;
			default:
				return super.onContextItemSelected(menu, item);
		}
	}

	public List<CineworldCinema> retrieve() {
		return new CineworldAccessor().getAllCinemas();
	}

	public List<CineworldCinema> process(final List<CineworldCinema> list) {
		Geocoder coder = new Geocoder(this);
		for (CineworldCinema cinema : list) {
			try {
				GeoPoint loc = CinemasMapActivity.s_locations.get(cinema.getPostcode());
				if (loc == null) {
					List<Address> locs = coder.getFromLocationName(cinema.getPostcode(), 1);
					if (!locs.isEmpty()) {
						Address address = locs.get(0);
						loc = new GeoPoint((int) (address.getLatitude() * 1e6), (int) (locs.get(0).getLongitude() * 1e6));
					}
				}
				cinema.setLocation(loc);
			} catch (IOException ex) {
				Log.e("GEO", "Cannot get cinema location", ex);
			}
		}
		return list;
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldCinema> result) {
		return new CinemaAdapter(this, result);
	}

	@Override
	public void update(final List<CineworldCinema> result) {
		super.update(result);
		for (CineworldCinema cinema : result) {
			if (cinema.getLocation() != null) {
				String snippet = String.format("%s, %s", cinema.getAddress(), cinema.getPostcode());
				m_overlay.addItem(cinema.getLocation(), cinema.getName(), snippet);
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
			for (CineworldCinema cinema : adapter.getItems()) {
				if (cinema.getLocation() == newFocus.getPoint()) {
					break;
				}
				i++;
			}
			getSpinner().setSelection(i);
		} else {
			getSpinner().setSelection(0); // TODO
		}
		m_map.postInvalidate();
	}

	public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (skipEvent) {
			skipEvent = false;
			return;
		}
		CineworldCinema cinema = (CineworldCinema) parent.getAdapter().getItem(position);
		for (OverlayItem item : m_overlay.getItems()) {
			if (cinema.getLocation() == item.getPoint()) {
				m_overlay.setFocus(item);
				return;
			}
		}
		m_overlay.setFocus(null);
	}

	public void onNothingSelected(final AdapterView<?> parent) {
		m_overlay.setFocus(null);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
