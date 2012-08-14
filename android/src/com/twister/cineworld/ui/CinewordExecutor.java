package com.twister.cineworld.ui;

import java.util.concurrent.*;

public class CinewordExecutor {

	private static ExecutorService	EXEC	= Executors.newCachedThreadPool();

	private CinewordExecutor() {
		// utility class
	}

	public static void execute(final LoggedRunnable task) {
		CinewordExecutor.EXEC.execute(task);
	}

}
