package com.twister.cineworld.model.generic;

import java.util.*;

import com.google.android.maps.GeoPoint;

class GeoCache {

	private static final Map<String, GeoPoint>	s_locations	= new HashMap<String, GeoPoint>(79, 1f);
	static {
		// TODO these should come from a config file of some sort
		GeoCache.s_locations.put("AB245EN", new GeoPoint(57150274, -2077960));
		GeoCache.s_locations.put("AB115RG", new GeoPoint(57143560, -2096900));
		GeoCache.s_locations.put("TN254BN", new GeoPoint(51161418, 871635));
		GeoCache.s_locations.put("OL70PG", new GeoPoint(53489036, -2111578));
		GeoCache.s_locations.put("MK419LW", new GeoPoint(52133311, -445862));
		GeoCache.s_locations.put("DA67LL", new GeoPoint(51456276, 150038));
		GeoCache.s_locations.put("B151DA", new GeoPoint(52474393, -1914670));
		GeoCache.s_locations.put("NE359PB", new GeoPoint(54946785, -1466316));
		GeoCache.s_locations.put("BL18TS", new GeoPoint(53597185, -2423956));
		GeoCache.s_locations.put("BD15LD", new GeoPoint(53791609, -1747755));
		GeoCache.s_locations.put("CM778YH", new GeoPoint(51869979, 570965));
		GeoCache.s_locations.put("BN25UF", new GeoPoint(50812637, -100692));
		GeoCache.s_locations.put("BS140HR", new GeoPoint(51416347, -2587139));
		GeoCache.s_locations.put("DE141NQ", new GeoPoint(52805594, -1632463));
		GeoCache.s_locations.put("IP333BA", new GeoPoint(52246199, 706604));
		GeoCache.s_locations.put("CB17DY", new GeoPoint(52190163, 136962));
		GeoCache.s_locations.put("CF102EN", new GeoPoint(51478735, -3173395));
		GeoCache.s_locations.put("WF104TA", new GeoPoint(53710173, -1341896));
		GeoCache.s_locations.put("SW35EW", new GeoPoint(51485333, -173551));
		GeoCache.s_locations.put("GL504EF", new GeoPoint(51902925, -2075025));
		GeoCache.s_locations.put("CH14QQ", new GeoPoint(53197686, -2917127));
		GeoCache.s_locations.put("S402ED", new GeoPoint(53227146, -1424189));
		GeoCache.s_locations.put("PO198EL", new GeoPoint(50830018, -785081));
		GeoCache.s_locations.put("RH108LR", new GeoPoint(51120235, -189645));
		GeoCache.s_locations.put("OX117ND", new GeoPoint(51607633, -1238501));
		GeoCache.s_locations.put("M205PG", new GeoPoint(53407988, -2220440));
		GeoCache.s_locations.put("DD24TF", new GeoPoint(56484392, -3046190));
		GeoCache.s_locations.put("BN236JH", new GeoPoint(50794853, 322845));
		GeoCache.s_locations.put("EH111AF", new GeoPoint(55941939, -3216935));
		GeoCache.s_locations.put("EN11YQ", new GeoPoint(51650908, -61218));
		GeoCache.s_locations.put("FK11LW", new GeoPoint(56003773, -3779903));
		GeoCache.s_locations.put("TW137LX", new GeoPoint(51443588, -406448));
		GeoCache.s_locations.put("SW109QR", new GeoPoint(51487151, -179428));
		GeoCache.s_locations.put("G314EB", new GeoPoint(55853441, -4199821));
		GeoCache.s_locations.put("G23AB", new GeoPoint(55864904, -4255177));
		GeoCache.s_locations.put("GL15SF", new GeoPoint(51857595, -2253618));
		GeoCache.s_locations.put("W69JT", new GeoPoint(51492525, -233306));
		GeoCache.s_locations.put("CM202DA", new GeoPoint(51783876, 108064));
		GeoCache.s_locations.put("CB90ER", new GeoPoint(52083648, 439979));
		GeoCache.s_locations.put("SW1Y4RL", new GeoPoint(51508803, -132393));
		GeoCache.s_locations.put("HP112DB", new GeoPoint(51630805, -756212));
		GeoCache.s_locations.put("HU73DB", new GeoPoint(53792485, -352993));
		GeoCache.s_locations.put("PE297EG", new GeoPoint(52351409, -180699));
		GeoCache.s_locations.put("IG11BP", new GeoPoint(51557634, 74113));
		GeoCache.s_locations.put("IP11AX", new GeoPoint(52053221, 1151000));
		GeoCache.s_locations.put("PO302TA", new GeoPoint(50698962, -1289230));
		GeoCache.s_locations.put("JE24HE", new GeoPoint(54617791, -107384442));
		GeoCache.s_locations.put("WN74PE", new GeoPoint(53494022, -2515212));
		GeoCache.s_locations.put("L131EW", new GeoPoint(53408399, -2922888));
		GeoCache.s_locations.put("LL319XX", new GeoPoint(53282271, -3808416));
		GeoCache.s_locations.put("LU12NB", new GeoPoint(51881848, -417659));
		GeoCache.s_locations.put("TS12DY", new GeoPoint(54574656, -1226261));
		GeoCache.s_locations.put("MK93XS", new GeoPoint(52041427, -748697));
		GeoCache.s_locations.put("NP194QQ", new GeoPoint(51577570, -2943582));
		GeoCache.s_locations.put("NN55QJ", new GeoPoint(52235218, -936596));
		GeoCache.s_locations.put("NG14AA", new GeoPoint(52955403, -1149805));
		GeoCache.s_locations.put("ME22SS", new GeoPoint(51380031, 477083));
		GeoCache.s_locations.put("CV211RW", new GeoPoint(52385225, -1259181));
		GeoCache.s_locations.put("WA72FQ", new GeoPoint(53326183, -2699968));
		GeoCache.s_locations.put("W1D7DH", new GeoPoint(51510953, -133011));
		GeoCache.s_locations.put("S92EP", new GeoPoint(53401520, -1414966));
		GeoCache.s_locations.put("SY37ET", new GeoPoint(52703534, -2740530));
		GeoCache.s_locations.put("B913GS", new GeoPoint(52412403, -1779387));
		GeoCache.s_locations.put("SO143TJ", new GeoPoint(50895193, -1394938));
		GeoCache.s_locations.put("WA101BF", new GeoPoint(53451370, -2740228));
		GeoCache.s_locations.put("NW26LW", new GeoPoint(51570291, -229715));
		GeoCache.s_locations.put("SG12UA", new GeoPoint(51899793, -208623));
		GeoCache.s_locations.put("SK13TA", new GeoPoint(53406906, -2160912));
		GeoCache.s_locations.put("SN57DN", new GeoPoint(51560797, -1831333));
		GeoCache.s_locations.put("SE100DX", new GeoPoint(51501570, 5531));
		GeoCache.s_locations.put("WF29SH", new GeoPoint(53677148, -1506104));
		GeoCache.s_locations.put("SW184TF", new GeoPoint(51455402, -194192));
		GeoCache.s_locations.put("E144AL", new GeoPoint(51507645, -23844));
		GeoCache.s_locations.put("DT48LY", new GeoPoint(50610237, -2455119));
		GeoCache.s_locations.put("OX286GW", new GeoPoint(51787148, -1486060));
		GeoCache.s_locations.put("WV111TZ", new GeoPoint(52596272, -2093200));
		GeoCache.s_locations.put("N226LU", new GeoPoint(51594544, -106456));
		GeoCache.s_locations.put("BA201NP", new GeoPoint(50940369, -2625528));
	}

	private GeoCache() {
		// prevent instantiation
	}

	/**
	 * Get a {@link GeoPoint} object from cache.
	 * 
	 * @param postCode the postcode
	 * @return the cached {@link GeoPoint}
	 */
	public static GeoPoint getGeoPoint(final String postCode) {
		return postCode != null? GeoCache.s_locations.get(postCode.replaceAll("\\s+", "").toUpperCase()) : null;
	}

}
