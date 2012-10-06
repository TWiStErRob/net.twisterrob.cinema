package com.twister.cineworld.model.generic;

public class GenericBase {
	private String	source;

	/**
	 * Where is this Object originated from (i.e. who created it)?
	 * 
	 * @return the source of this object
	 */
	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}
}
