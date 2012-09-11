package com.twister.cineworld.exception;

public class NetworkException extends CineworldException {

	private static final long	serialVersionUID	= 2106172394798240654L;

	public NetworkException(final String message) {
		super(ErrorType.NETWORK, message, null);
	}

	public NetworkException(final String message, final Exception cause) {
		super(ErrorType.NETWORK, message, cause, null);
	}

}
