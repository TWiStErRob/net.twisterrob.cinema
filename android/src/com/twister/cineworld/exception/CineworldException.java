package com.twister.cineworld.exception;

/**
 * Common base class for exceptions in the application.
 * 
 * @author Zoltán Kiss
 */
public abstract class CineworldException extends Exception {

	private static final long	serialVersionUID	= -295518164973462329L;

	private final ErrorType		type;

	private final Object[]		params;

	public CineworldException(final ErrorType type, final Object... params) {
		super();
		this.type = type;
		this.params = params;
	}

	public CineworldException(final ErrorType type, final Exception cause, final Object... params) {
		this(type, cause.getMessage(), cause, params);
	}

	public CineworldException(final ErrorType type, final String message, final Exception cause, final Object... params) {
		super(message, cause);
		this.type = type;
		this.params = params;
	}

	/**
	 * Error type which caused the exception. The error type defines the message which gets printed to the user when the
	 * exception is reported.
	 * 
	 * @return
	 */
	public ErrorType getType() {
		return type;
	}

	/**
	 * Parameters for the report text about this exception. Report texts are format strings for
	 * {@link String#format(String, Object...)}, which get their parameters from this array.
	 * 
	 * @return
	 */
	public Object[] getParams() {
		return params;
	}

}
