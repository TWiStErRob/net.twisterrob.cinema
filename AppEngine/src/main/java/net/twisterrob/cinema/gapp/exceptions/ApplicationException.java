package net.twisterrob.cinema.gapp.exceptions;

public abstract class ApplicationException extends Exception {
	private static final long serialVersionUID = 7107647218824821957L;

	public ApplicationException() {
		super();
	}

	public ApplicationException(final String message, final Object... messageParams) {
		super(String.format(message, messageParams));
	}

	public ApplicationException(final Throwable cause) {
		super(cause);
	}

	public ApplicationException(final String message, final Throwable cause, final Object... messageParams) {
		super(String.format(message, messageParams), cause);
	}
}
