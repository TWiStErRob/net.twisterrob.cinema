package com.twister.cineworld.model.accessor.impl.util;

import java.io.IOException;
import java.util.*;

import android.location.*;

import com.google.android.maps.GeoPoint;
import com.twister.cineworld.App;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.model.generic.Location;

public class GeoCache {
	private static final Log					LOG			= LogFactory.getLog(Tag.GEO);
	private static final Map<String, Location>	s_locations	= new HashMap<String, Location>(79, 1f);
	static {
		List<PostCodeLocation> locations = App.getInstance().getDataBaseHelper().getGeoCacheLocations();
		for (PostCodeLocation location : locations) {
			String postCode = GeoCache.fixPostCode(location.getPostCode());
			s_locations.put(postCode, location.getLocation());
		}
	}

	private GeoCache() {
		// prevent instantiation
	}

	private static String fixPostCode(final String postCode) {
		return postCode.replaceAll("\\s+", "").toUpperCase();
	}

	/**
	 * Get a {@link GeoPoint} object from cache.
	 * 
	 * @param postCode the postcode
	 * @return the cached {@link GeoPoint}
	 */
	public static Location getGeoPoint(String postCode) {
		if (postCode == null) {
			return null;
		}
		postCode = GeoCache.fixPostCode(postCode);
		Location loc = s_locations.get(postCode);
		if (loc == null) {
			loc = GeoCache.geoCode(postCode);
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
}
