package com.twister.cineworld.exception;


public class NetworkException extends ApplicationException {
	private static final long serialVersionUID = 2106172394798240654L;

	public NetworkException(final String messageFormat, final Object... formatArgs) {
		super(ErrorType.NETWORK, String.format(messageFormat, formatArgs), (Object[])null);
	}

	public NetworkException(final String messageFormat, final Exception cause,
			final Object... formatArgs) {
		super(ErrorType.NETWORK, String.format(messageFormat, formatArgs), cause, (Object[])null);
	}
}
