package com.twister.cineworld.ui;

import java.util.concurrent.atomic.AtomicLong;

import com.twister.cineworld.App;
import com.twister.cineworld.log.*;

/**
 * Runnable implementation which logs escaping {@link RuntimeException}s.
 * 
 * @author Zoltán Kiss
 */
public abstract class LoggedRunnable implements Runnable {
	private static final Log		LOG			= LogFactory.getLog(Tag.SYSTEM);
	private static final String		LOG_FORMAT	= "%3$s (#%2$s/%4$s) %1$s: %5$s";
	private static final String		SIMPLE_NAME	= LoggedRunnable.class.getSimpleName();
	private static final AtomicLong	s_seq		= new AtomicLong(System.currentTimeMillis());

	protected final String			taskId;

	public LoggedRunnable() {
		this.taskId = Long.toHexString(s_seq.getAndIncrement());
	}

	public final void run() {
		LOG.verbose(LOG_FORMAT, "started", taskId, SIMPLE_NAME, getClass().getName(), whatAmIDoing());
		App.reportStatus("%s: %s", "started", whatAmIDoing());
		try {
			loggedRun();
		} catch (RuntimeException ex) {
			LOG.wtf(LOG_FORMAT, ex, "errored", taskId, SIMPLE_NAME, getClass().getName(), whatAmIDoing());
			App.reportStatus("%s: %s", "errored: " + ex.getMessage(), whatAmIDoing());
		} finally {
			LOG.verbose(LOG_FORMAT, "finished", taskId, SIMPLE_NAME, getClass().getName(), whatAmIDoing());
			App.reportStatus("%s: %s", "finished", whatAmIDoing());
		}
	}

	/**
	 * A substitution method for {@link Runnable#run()}. This method is called by {@link #run()}, which wraps the call
	 * into a try-catch block and catches and logs out escaping {@link RuntimeException}s
	 */
	protected abstract void loggedRun();

	protected abstract String whatAmIDoing();
}
