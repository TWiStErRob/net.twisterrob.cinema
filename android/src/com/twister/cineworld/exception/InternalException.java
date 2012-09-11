package com.twister.cineworld.exception;

public class InternalException extends CineworldException {

	private static final long	serialVersionUID	= 7260039205300791716L;

	public InternalException(final String message) {
		super(ErrorType.INTERNAL, message, null);
	}

	public InternalException(final Exception cause) {
		super(ErrorType.INTERNAL, cause, null);
	}

	public InternalException(final String message, final Exception cause) {
		super(ErrorType.INTERNAL, message, cause, null);
	}

}
