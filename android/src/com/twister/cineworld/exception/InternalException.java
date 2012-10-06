package com.twister.cineworld.exception;

import com.twister.cineworld.tools.StringTools;

public class InternalException extends ApplicationException {
	private static final long	serialVersionUID	= 7260039205300791716L;

	public InternalException(final String messageFormat, final Object... formatArgs) {
		super(ErrorType.INTERNAL, StringTools.format(messageFormat, formatArgs), (Object[]) null);
	}

	public InternalException(final Exception cause) {
		super(ErrorType.INTERNAL, cause, (Object[]) null);
	}

	public InternalException(final String messageFormat, final Exception cause, final Object... formatArgs) {
		super(ErrorType.INTERNAL, StringTools.format(messageFormat, formatArgs), cause, (Object[]) null);
	}
}
