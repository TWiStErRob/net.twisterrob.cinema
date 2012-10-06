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
	private static final AtomicLong	SEQ			= new AtomicLong(System.currentTimeMillis());

	protected final String			taskId;

	public LoggedRunnable() {
		this.taskId = Long.toHexString(LoggedRunnable.SEQ.getAndIncrement());
	}

	public final void run() {
		if (LoggedRunnable.LOG.isVerboseEnabled()) {
			LoggedRunnable.LOG.verbose(LoggedRunnable.LOG_FORMAT, "started", taskId,
					LoggedRunnable.class.getSimpleName(), getClass().getName());
		}
		try {
			loggedRun();
		} catch (RuntimeException ex) {
			LoggedRunnable.LOG.wtf("Uncaught exception in %s #%s", ex, LoggedRunnable.class.getSimpleName(), taskId);
		} finally {
			if (LoggedRunnable.LOG.isVerboseEnabled()) {
				LoggedRunnable.LOG.verbose(LoggedRunnable.LOG_FORMAT, "finished", taskId,
						LoggedRunnable.class.getSimpleName(), getClass().getName());
			}
		}
	}

	/**
	 * A substitution method for {@link Runnable#run()}. This method is called by {@link #run()}, which wraps the call
	 * into a try-catch block and catches and logs out escaping {@link RuntimeException}s
	 */
	protected abstract void loggedRun();

}
