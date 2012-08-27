package com.twister.cineworld.log;

public enum Tag {
	JSON,
	ACCESS,
	UI,
	GEO,
	SYSTEM;

	private static final String	TAG_PREFIX	= "cw.";

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
