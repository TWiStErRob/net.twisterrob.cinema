package com.twister.cineworld.exception;

import com.twister.cineworld.ui.Translated;

/**
 * Common base class for exceptions in the application.
 * 
 * @author Zoltán Kiss
 */
public abstract class CineworldException extends Exception implements Translated {

	private static final long	serialVersionUID	= -295518164973462329L;

	private final ErrorType		type;

	private final Object[]		params;

	public CineworldException(final ErrorType type, final Object[] params) {
		super();
		checkType(type);
		this.type = type;
		this.params = params;
	}

	public CineworldException(final ErrorType type, final String message, final Object[] params) {
		super(message);
		checkType(type);
		this.type = type;
		this.params = params;
	}

	public CineworldException(final ErrorType type, final Exception cause, final Object[] params) {
		super(cause);
		checkType(type);
		this.type = type;
		this.params = params;
	}

	public CineworldException(final ErrorType type, final String message, final Exception cause, final Object[] params) {
		super(message, cause);
		checkType(type);
		this.type = type;
		this.params = params;
	}

	private void checkType(final ErrorType type) {
		if (type == null) {
			throw new IllegalArgumentException(String.format("%s (%s) is mandatory", "type", ErrorType.class));
		}
	}

	public int getResId() {
		return this.type.getMessageResourceId();
	}

	public Object[] getParams() {
		return params;
	}

}
