package com.twister.cineworld.tools;

import java.util.Iterator;

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

	public static String join(final Iterable<?> list, final String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> iterator = list.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next());
			if (iterator.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
}
