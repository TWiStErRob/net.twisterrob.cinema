package com.twister.cineworld.ui;

import java.util.concurrent.atomic.AtomicLong;

import com.twister.cineworld.log.*;

/**
 * Runnable implementation which logs escaping {@link RuntimeException}s.
 * 
 * @author Zoltán Kiss
 */
public abstract class LoggedRunnable implements Runnable {
	private static final Log		LOG			= LogFactory.getLog(Tag.SYSTEM);
	private static final String		LOG_FORMAT	= "%3$s (#%2$s) %1$s: %4$s";
	private static final String		SIMPLE_NAME	= LoggedRunnable.class.getSimpleName();
	private static final AtomicLong	s_seq		= new AtomicLong(System.currentTimeMillis());

	protected final String			taskId;

	public LoggedRunnable() {
		this.taskId = Long.toHexString(s_seq.getAndIncrement());
	}

	public final void run() {
		LOG.verbose(LOG_FORMAT, "started", taskId, SIMPLE_NAME, getClass().getName());
		try {
			loggedRun();
		} catch (RuntimeException ex) {
			LOG.wtf("Uncaught exception in %s #%s", ex, SIMPLE_NAME, taskId);
		} finally {
			LOG.verbose(LOG_FORMAT, "finished", taskId, SIMPLE_NAME, getClass().getName());
		}
	}

	/**
	 * A substitution method for {@link Runnable#run()}. This method is called by {@link #run()}, which wraps the call
	 * into a try-catch block and catches and logs out escaping {@link RuntimeException}s
	 */
	protected abstract void loggedRun();
}
