package com.twister.cineworld.ui;

import android.app.Activity;

import com.twister.cineworld.exception.*;

/**
 * Base class for background tasks which have to be presented on the GUI when they finish.
 * 
 * @author Zoltán Kiss
 * @param <R>
 */
public abstract class CinewordGUITask<R> extends LoggedRunnable {

	protected final Activity	activity;

	public CinewordGUITask(final Activity activity) {
		this.activity = activity;
	}

	@Override
	protected final void execute() {
		try {
			final R result = work();
			this.activity.runOnUiThread(new LoggedRunnable() {
				@Override
				protected void execute() {
					present(result);
				}
			});
		} catch (CineworldException e) {
			handleException(e);
		} catch (RuntimeException e) {
			handleException(new InternalException(e));
		}
	}

	private void handleException(final CineworldException e) {
		/*
		 * TODO Talán itt lenne érdemes csinálni egy általános metódust valami error dialog meghívására és akkor
		 * konzisztensen viselkedne az alkalmazás. Ekkor az exception metódusban csak az Activity specifikus dolgokat
		 * kellene elvégezni pl form ürítése vagy akármi...
		 */
		activity.runOnUiThread(new LoggedRunnable() {
			@Override
			protected void execute() {
				exception(e);
			}
		});
	}

	/**
	 * Do the main work. This is typically a long running operation which has some kind of results. This method is
	 * executed on a background thread, no GUI calls are allowed here.
	 * 
	 * @return The results of the work
	 * @throws CineworldException if any error occurs
	 */
	protected abstract R work() throws CineworldException;

	/**
	 * Presents the results of the work. This callback is executed on a GUI thread. This method should not do any
	 * calculations, just plain presentation.
	 * 
	 * @param result
	 */
	protected abstract void present(R result);

	/**
	 * Exception handler callback. Whenever an exception occurs, this method is called on a GUI thread.
	 * 
	 * @param e
	 */
	protected abstract void exception(CineworldException e);

}
