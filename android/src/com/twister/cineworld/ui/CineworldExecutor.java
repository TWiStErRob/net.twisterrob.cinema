package com.twister.cineworld.ui;

import java.util.concurrent.*;

/**
 * An convenience utility class to acces the global {@link ExecutorService} instance assigned to the application.
 * 
 * @author Zoltán Kiss
 */
public class CineworldExecutor {

	private static ExecutorService	EXEC	= Executors.newCachedThreadPool();

	private CineworldExecutor() {
		// utility class
	}

	/**
	 * Submit a task for asynchronous execution.
	 * 
	 * @param task the task to execute
	 * @see ExecutorService#execute(Runnable)
	 */
	public static void execute(final LoggedRunnable task) {
		CineworldExecutor.EXEC.execute(task);
	}

}
