package com.twister.cineworld.exception;


/**
 * Differentiated errors which cause exceptions in the application and get reported back
 * to the user. Each {@link ErrorType} has it's own message text localized as a format
 * string for {@link String#format(String, Object...)}
 * 
 * @author Zoltán Kiss
 */
enum ErrorType {
	/**
	 * A network error occurred. It is possible that your Internet connection is down.
	 * <p>
	 * <b>No Parameters</b>
	 * </p>
	 */
	NETWORK(0),
	/**
	 * An internal error occurred. This may be a bug in the application, apologies for
	 * that.
	 * <p>
	 * <b>No Parameters</b>
	 * </p>
	 */
	INTERNAL(1),
	/**
	 * An error occurred in one of the systems we depend on: %1$s. This probably means a
	 * malfunction in that system, please try again later.
	 * <p>
	 * <b>Parameters</b>:
	 * <ol>
	 * <li>Malfunctioning system</li>
	 * </ol>
	 * </p>
	 */
	EXTERNAL(2);

	private int m_messageResourceId;

	private ErrorType(final int messageResourceId) {
		m_messageResourceId = messageResourceId;
	}

	public int getMessageResourceId() {
		return m_messageResourceId;
	}
}
