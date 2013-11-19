package com.twister.cineworld.exception;

public class ExternalException extends ApplicationException {
	private static final long serialVersionUID = 6036021854062972178L;

	public ExternalException(final System system) {
		super(ErrorType.EXTERNAL, new Object[]{system});
	}

	public ExternalException(final System system, final String messageFormat, final Object... formatArgs) {
		super(ErrorType.EXTERNAL, String.format(messageFormat, formatArgs), new Object[]{system});
	}

	public ExternalException(final System system, final String messageFormat, final Exception cause,
			final Object... formatArgs) {
		super(ErrorType.EXTERNAL, String.format(messageFormat, formatArgs), cause, new Object[]{system});
	}

	public enum System {
		CINEWORLD;
	}
}
