package com.twister.cineworld.exception;

import com.twister.cineworld.R;

/**
 * Differentiated errors which cause exceptions in the application and get reported back to the user. Each
 * {@link ErrorType} has it's own message text localized as a format string for {@link String#format(String, Object...)}
 * 
 * @author Zoltán Kiss
 */
public enum ErrorType {
	/**
	 * A network error occured. It is possible that your internet connection is down.<br>
	 * <b>Additional parameters</b>: 0.
	 */
	NETWORK(R.string.error_network),
	/**
	 * An internal error occured. This may be a bug in the application, apologies for that.<br>
	 * <b>Additional parameters</b>: 0.
	 */
	INTERNAL(R.string.error_internal);

	private int	m_messageResourceId;

	private ErrorType(final int messageResourceId) {
		m_messageResourceId = messageResourceId;
	}

	public int getMessageResourceId() {
		return m_messageResourceId;
	}
}
