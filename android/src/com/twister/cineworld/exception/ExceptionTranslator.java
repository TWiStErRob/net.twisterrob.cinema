package com.twister.cineworld.exception;

import android.content.Context;

/**
 * Provices utility methods for extracting messages which are to be reported to the user from a {@link CineworldException}
 * 
 * @author Zoltán Kiss
 */
public abstract class ExceptionTranslator {

	private ExceptionTranslator() {
		// utility class
	}

	/**
	 * Extract a formatted string from a {@link CineworldException}
	 * 
	 * @param context {@link Context} to use for resource lookup
	 * @param exception the {@link CineworldException} which needs to be explained
	 * @return A localized, parameterized string which explains to the user why the exception happened.
	 */
	public static String getExceptionExplanation(final Context context, final CineworldException exception) {
		/*
		 * TODO Erre nem tudom szükség van-e, lehet hogy elég a resource ID-t meghatározni és a paraméterek átadhatóak a GUI elemnek is ami a szöveget jeleníti
		 * meg...
		 */
		return context.getString(exception.getType().getMessageResourceId(), exception.getParams());
	}
}
