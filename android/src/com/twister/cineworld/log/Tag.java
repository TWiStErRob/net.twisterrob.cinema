package com.twister.cineworld.log;

import android.util.Log;

/**
 * Available tag strings in the application logs.
 * 
 * @author Zoltán Kiss
 */
public enum Tag {
	/*
	 * Bear in mind that the length of tags cannot be longer than 23 characters according to the Android API.
	 */
	JSON,
	ACCESS,
	UI,
	GEO,
	SYSTEM;

	private static final int	TAG_MAX_LENGTH	= 23;

	private static final String	TAG_PREFIX		= "cw.";

	static {
		/*
		 * Check tag string lengths
		 */
		for (Tag tag : Tag.values()) {
			if (tag.getTag().length() > Tag.TAG_MAX_LENGTH) {
				Log.w(null, "Tag value is longer than 23 chars: " + tag.name() + "=" + tag.getTag());
			}
		}
	}

	private final String		m_tag;

	private Tag() {
		this(null);
	}

	private Tag(final String tag) {
		m_tag = Tag.TAG_PREFIX + (tag == null? name() : tag);
	}

	public String getTag() {
		return m_tag;
	}

}
