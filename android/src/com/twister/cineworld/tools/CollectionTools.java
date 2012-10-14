package com.twister.cineworld.tools;

import java.util.*;

public final class CollectionTools {
	private CollectionTools() {
		// prevent instantiation
	}

	public static void ensureIndexValid(final List<? extends Object> list, int i) {
		i -= list.size();
		while (i-- >= 0) {
			list.add(null);
		}
	}

	public static <T> T coalesce(final T... objects) {
		for (T t : objects) {
			if (t != null) {
				return t;
			}
		}
		return null;
	}

	public static <T> List<T> remove(final List<T> list, final Class<? extends T> clazz) {
		List<T> removed = new LinkedList<T>();
		Iterator<T> it = list.listIterator();
		while (it.hasNext()) {
			T item = it.next();
			if (clazz.isAssignableFrom(item.getClass())) {
				it.remove();
				removed.add(item);
			}
		}
		return removed;
	}
}
