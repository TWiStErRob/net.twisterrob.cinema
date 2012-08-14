package com.twister.cineworld.exception;

import android.content.Context;
import android.util.Log;

import com.twister.cineworld.R;

/**
 * Provices utility methods for extracting messages which are to be reported to the user from a
 * {@link CineworldException}
 * 
 * @author Zoltán Kiss
 */
public class ExceptionTranslator {

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
		 * TODO Erre nem tudom szükség van-e, lehet hogy elég a resource ID-t meghatározni és a paraméterek átadhatóak a
		 * GUI elemnek is ami a szöveget jeleníti meg...
		 */
		int resId = ExceptionTranslator.getMessageResource(exception.getType());
		return context.getString(resId, exception.getParams());
	}

	/**
	 * Find the string resource associated with an {@link ErrorType}.
	 * 
	 * @param type
	 * 
	 * @return
	 */
	public static int getMessageResource(final ErrorType type) {
		int ret;
		switch (type) {
			case INTERNAL:
				ret = R.string.error_internal;
				break;
			case NETWORK:
				ret = R.string.error_network;
				break;
			default:
				Log.wtf(ExceptionTranslator.class.getSimpleName(), "ErrorType not mapped: " + type.name());
				ret = R.string.error_internal;
		}
		return ret;
	}

}
