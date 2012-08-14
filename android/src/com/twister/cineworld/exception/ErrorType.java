package com.twister.cineworld.exception;

/**
 * Differentiated errors which cause exceptions in the application and get reported back to the user. Each
 * {@link ErrorType} has it's own message text localized as a format string for {@link String#format(String, Object...)}
 * 
 * @author Zoltán Kiss
 */
public enum ErrorType {
	/**
	 * A network error occured. It is possible that your internet connection is down.
	 */
	NETWORK,
	/**
	 * An internal error occured. This may be a bug in the application, apologies for that.
	 */
	INTERNAL,
}
