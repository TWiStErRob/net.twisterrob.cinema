package com.twister.cineworld.log;

/**
 * Class to hide the complexity of Logging in android.
 * 
 * @author Zoltán Kiss
 */
public class Log {
	private final String	m_tag;

	/**
	 * Constructs a logger class.
	 * 
	 * @param tag the tag to use in {@link android.util.Log} calls.
	 */
	Log(final Tag tag) {
		m_tag = tag.getTag();
	}

	public boolean isVerboseEnabled() {
		return android.util.Log.isLoggable(m_tag, android.util.Log.VERBOSE);
	}

	public boolean isDebugEnabled() {
		return android.util.Log.isLoggable(m_tag, android.util.Log.DEBUG);
	}

	public boolean isInfoEnabled() {
		return android.util.Log.isLoggable(m_tag, android.util.Log.INFO);
	}

	public void verbose(final String message) {
		android.util.Log.v(m_tag, message);
	}

	public void verbose(final String message, final Throwable t) {
		android.util.Log.v(m_tag, message, t);
	}

	public void debug(final String message) {
		android.util.Log.d(m_tag, message);
	}

	public void debug(final String message, final Throwable t) {
		android.util.Log.d(m_tag, message, t);
	}

	public void info(final String message) {
		android.util.Log.i(m_tag, message);
	}

	public void info(final String message, final Throwable t) {
		android.util.Log.i(m_tag, message, t);
	}

	public void warn(final String message, final Object... params) {
		android.util.Log.w(m_tag, String.format(message, params));
	}

	public void warn(final Throwable t, final String message, final Object... params) {
		android.util.Log.w(m_tag, String.format(message, params), t);
	}

	public void error(final String message) {
		android.util.Log.e(m_tag, message);
	}

	public void error(final String message, final Throwable t) {
		android.util.Log.e(m_tag, message, t);
	}

	public void wtf(final String message) {
		android.util.Log.wtf(m_tag, message);
	}

	public void wtf(final String message, final Throwable t) {
		android.util.Log.wtf(m_tag, message, t);
	}
}
