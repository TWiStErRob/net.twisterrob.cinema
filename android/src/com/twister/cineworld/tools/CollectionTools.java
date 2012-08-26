package com.twister.cineworld.tools;

import java.util.List;

public class CollectionTools {
	public static void ensureIndexValid(final List<? extends Object> list, int i) {
		i -= list.size();
		while (i-- >= 0) {
			list.add(null);
		}
	}
}
