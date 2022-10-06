package net.twisterrob.test;

public class TestUtils {
	/**
	 * Sleeps the given millies using {@link Thread#sleep(long)}, but catches the exception.<br>
	 * Use sparingly. It's adviesd to add <code>throws Exception</code> to your test method.
	 */
	public static void sleep(long millies) {
		try {
			Thread.sleep(millies);
		} catch (InterruptedException ex) {
			// ignored
		}
	}
}
