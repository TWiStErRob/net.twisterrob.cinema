package com.twister.cineworld.ui;

import android.content.Context;

/**
 * Provices utility methods for extracting texts which are to be presented to the user from a {@link Translated} object
 * 
 * @author Zoltán Kiss
 */
public class Translator {

	private static final Object[]	EMPTY	= new Object[0];

	private Translator() {
		// prevent instantiation
	}

	/**
	 * Extract a formatted string from a {@link Translated} object
	 * 
	 * @param context {@link Context} to use for resource lookup
	 * @param translated the {@link Translated} which needs to be printed
	 * @return A localized, parameterized string which contains the text the parameter contains
	 */
	public static String translate(final Context context, final Translated translated) {
		Object[] params = translated.getParams();
		params = params != null? params : Translator.EMPTY;
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof Translated) {
				params[i] = Translator.translate(context, (Translated) params[i]);
			}
		}
		return context.getString(translated.getResId(), params);
	}
}
