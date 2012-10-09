package com.twister.cineworld.ui.activity;

import com.twister.cineworld.model.generic.*;

public interface UIRequestExtras {
	public static final String	EXTRA_PREFIX		= "extra_";
	/**
	 * Cinema<br>
	 * <b>Type</b>: {@link Cinmea}
	 */
	public static final String	EXTRA_CINEMA		= EXTRA_PREFIX + "cinema";
	/**
	 * Film<br>
	 * <b>Type</b>: {@link Film}
	 */
	public static final String	EXTRA_FILM			= EXTRA_PREFIX + "film";
	/**
	 * Distributor<br>
	 * <b>Type</b>: {@link Distributor}
	 */
	public static final String	EXTRA_DISTRIBUTOR	= EXTRA_PREFIX + "distributor";
	/**
	 * Event<br>
	 * <b>Type</b>: {@link Event}
	 */
	public static final String	EXTRA_EVENT			= EXTRA_PREFIX + "event";
	/**
	 * Category<br>
	 * <b>Type</b>: {@link Category}
	 */
	public static final String	EXTRA_CATEGORY		= EXTRA_PREFIX + "category";
}
