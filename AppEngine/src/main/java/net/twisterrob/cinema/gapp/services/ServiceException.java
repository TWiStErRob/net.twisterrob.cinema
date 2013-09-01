package net.twisterrob.cinema.gapp.services;

import net.twisterrob.cinema.gapp.exceptions.ApplicationException;

public class ServiceException extends ApplicationException {
	private static final long serialVersionUID = -2992497766539967421L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message, Object... messageParams) {
		super(message, messageParams);
	}

	public ServiceException(String message, Throwable cause, Object... messageParams) {
		super(message, cause, messageParams);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
