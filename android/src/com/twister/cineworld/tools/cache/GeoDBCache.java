package com.twister.cineworld.tools.cache;

import java.io.IOException;
import java.util.*;

import android.location.*;

import com.twister.cineworld.App;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.generic.Location;

public class GeoDBCache implements Cache<String, Location> {
	private static final Log			LOG			= LogFactory.getLog(Tag.GEO);
	private final Map<String, Location>	m_locations	= new HashMap<String, Location>(79, 1f);

	public GeoDBCache() {
		List<PostCodeLocation> locations = App.getInstance().getDataBaseHelper().getGeoCacheLocations();
		for (PostCodeLocation location : locations) {
			String postCode = GeoDBCache.fixPostCode(location.getPostCode());
			m_locations.put(postCode, location.getLocation());
		}
	}

	public Location get(final String key) {
		return getGeoPoint(key);
	}

	public void put(String key, final Location value) {
		key = GeoDBCache.fixPostCode(key);
		PostCodeLocation loc = new PostCodeLocation(key, value);
		App.getInstance().getDataBaseHelper().putGeoLocations(Collections.singleton(loc));
	}

	public Location getGeoPoint(String postCode) {
		if (postCode == null) {
			return null;
		}
		postCode = GeoDBCache.fixPostCode(postCode);
		Location loc = m_locations.get(postCode);
		if (loc == null) {
			loc = GeoDBCache.geoCode(postCode);
			put(postCode, loc);
		}
		return loc;
	}

	private static Location geoCode(final String postCode) {
		Geocoder coder = new Geocoder(App.getInstance());
		Location loc = null;
		try {
			List<Address> locs = coder.getFromLocationName(postCode, 1);
			if (!locs.isEmpty()) {
				Address address = locs.get(0);
				loc = new Location(address.getLatitude(), address.getLongitude());
			}
		} catch (IOException ex) {
			if ("Service not Available".equals(ex.getMessage())) {
				LOG.warn("Cannot locate postcode: %s; %s", postCode, ex.getMessage());
			} else {
				LOG.warn("Cannot locate postcode: %s", ex, postCode);
			}
		}
		return loc;
	}

	private static String fixPostCode(final String postCode) {
		return postCode.replaceAll("\\s+", "").toUpperCase();
	}
}
