package com.twister.cineworld.log;

import android.util.Log;

/**
 * Class to hide the complexity of Logging in android.
 * 
 * @author Zoltán Kiss
 */
public class CineworldLogger {

	private final String	m_tag;

	/**
	 * Constructs a logger class.
	 * 
	 * @param tag the tag to use in {@link Log} calls.
	 */
	protected CineworldLogger(final Tag tag) {
		m_tag = tag.getTag();
	}

	public boolean isVerboseEnabled() {
		return Log.isLoggable(m_tag, Log.VERBOSE);
	}

	public boolean isDebugEnabled() {
		return Log.isLoggable(m_tag, Log.DEBUG);
	}

	public boolean isInfoEnabled() {
		return Log.isLoggable(m_tag, Log.INFO);
	}

	public void verbose(final String message) {
		Log.v(m_tag, message);
	}

	public void verbose(final String message, final Throwable t) {
		Log.v(m_tag, message, t);
	}

	public void debug(final String message) {
		Log.d(m_tag, message);
	}

	public void debug(final String message, final Throwable t) {
		Log.d(m_tag, message, t);
	}

	public void info(final String message) {
		Log.i(m_tag, message);
	}

	public void info(final String message, final Throwable t) {
		Log.i(m_tag, message, t);
	}

	public void warn(final String message) {
		Log.w(m_tag, message);
	}

	public void warn(final String message, final Throwable t) {
		Log.w(m_tag, message, t);
	}

	public void error(final String message) {
		Log.e(m_tag, message);
	}

	public void error(final String message, final Throwable t) {
		Log.e(m_tag, message, t);
	}

	public void wtf(final String message) {
		Log.wtf(m_tag, message);
	}

	public void wtf(final String message, final Throwable t) {
		Log.wtf(m_tag, message, t);
	}

}
