package com.twister.cineworld.ui;

import android.util.Log;

/**
 * Runnable implementation which logs escaping {@link RuntimeException}s.
 * 
 * @author Zoltán Kiss
 */
public abstract class LoggedRunnable implements Runnable {

	public final void run() {
		try {
			execute();
		} catch (RuntimeException e) {
			Log.wtf(LoggedRunnable.class.getSimpleName(), "Uncaught exception", e);
		}
	}

	protected abstract void execute();

}
