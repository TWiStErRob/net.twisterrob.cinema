package com.twister.cineworld.ui.activity.maps;

import java.io.IOException;
import java.util.*;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;
import com.twister.cineworld.App;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.json.TimeSpan;
import com.twister.cineworld.tools.*;
import com.twister.cineworld.ui.*;

public class ItemizedOverlayCinemas extends ItemizedOverlay<OverlayItem> {
	private static final Log				LOG			= LogFactory.getLog(Tag.UI);

	private ArrayList<Cinema>				m_items		= new ArrayList<Cinema>();
	private Drawable						m_markerSelected;
	private MapView							m_map;
	private ItemizedOverlayFilmsForCinema	m_filmsOverlay;
	private ArrayList<OverlayItem>			m_itemCache	= new ArrayList<OverlayItem>();
	private Drawable						m_filmMarker;
	private Activity						m_activity;

	public ItemizedOverlayCinemas(final Activity activity, final MapView map, final Drawable marker,
			final Drawable markerSelected,
			final Drawable filmMarker) {
		super(marker);
		m_activity = activity;
		m_map = map;
		m_markerSelected = markerSelected;
		m_filmMarker = filmMarker;
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

	public List<Cinema> getItems() {
		return Collections.unmodifiableList(m_items);
	}

	@Override
	protected boolean onTap(final int index) {
		m_map.getOverlays().remove(m_filmsOverlay);
		if (index < 0 || size() <= index) {
			return false;
		}
		OverlayItem item = getItem(index);
		final Cinema cinema = m_items.get(index);
		item.setMarker(null);
		final ItemizedOverlayFilmsForCinema overlay = new ItemizedOverlayFilmsForCinema(m_map, cinema, m_filmMarker);
		m_filmsOverlay = overlay;
		CineworldExecutor.execute(new CineworldGUITask<List<Film>>(m_activity) {
			@Override
			protected List<Film> work() throws ApplicationException {
				Accessor accessor = App.getInstance().getCineworldAccessor();
				List<Film> films = accessor.getFilmsForCinema(cinema.getId(), TimeSpan.Tomorrow);
				for (Film film : films) {
					try {
						film.setPoster(IOTools.getImage(film.getPosterUrl()));
					} catch (IOException ex) {
						LOG.warn("Could not download film poster: %s", ex, film.getPosterUrl());
					}
				}
				return films;
			}

			@Override
			protected void present(final List<Film> result) {
				overlay.addAll(result);
			}

			@Override
			protected void exception(final ApplicationException e) {
				// TODO is there anything to do here?
			}

			@Override
			protected String whatAmIDoing() {
				return "load posters";
			}

		});
		m_map.getController().animateTo(cinema.getLocation().toGeoPoint());
		m_map.getOverlays().add(m_filmsOverlay);
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
