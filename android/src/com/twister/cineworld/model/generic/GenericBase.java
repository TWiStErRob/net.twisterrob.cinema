package com.twister.cineworld.model.generic;

import java.io.Serializable;

public class GenericBase implements Serializable {
	private static final long	serialVersionUID	= -3314136060033364263L;

	private String				source;

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
