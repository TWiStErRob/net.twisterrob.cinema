package com.twister.cineworld.tools;

import java.net.*;
import java.util.Iterator;

import com.twister.cineworld.exception.NetworkException;

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

	public static URL createUrl(final String type, final String... urls) throws NetworkException {
		String url = CollectionTools.coalesce(urls);
		if (url != null) {
			try {
				return new URL(url);
			} catch (MalformedURLException ex) {
				throw new NetworkException("Cannot associate %s Url: %s", ex, type, url);
			}
		}
		return null;
	}

	public static String toNullString(final Object o, final String nullString) {
		if (nullString == null) {
			return String.valueOf(o);
		} else {
			return o != null? o.toString() : nullString;
		}
	}
}
