package com.twister.cineworld.ui;

import com.twister.cineworld.R;

/**
 * An object which contains text which can directly go to the GUI
 * 
 * @author Zoltán Kiss
 */
public interface Translated {
	/**
	 * Returns the resource id for the text. Must be a valid id from {@link R.string}
	 * 
	 * @return
	 */
	public int getResId();

	/**
	 * Parameters for the text. Texts are format strings for {@link String#format(String, Object...)}, which get their
	 * parameters from this array.
	 * 
	 * @return <code>null</code>, if there are no parameters
	 */
	public Object[] getParams();
}
