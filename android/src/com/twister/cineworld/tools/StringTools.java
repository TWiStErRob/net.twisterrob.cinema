package com.twister.cineworld.tools;

public final class StringTools {
	private StringTools() {
		// prevent instantiation
	}

	public static String format(final String messageFormat, final Object... formatArgs) {
		if (formatArgs == null || formatArgs.length == 0) {
			return messageFormat;
		} else {
			return String.format(messageFormat, formatArgs);
		}
	}
}
