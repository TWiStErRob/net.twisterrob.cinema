package com.twister.cineworld.model.json.request;

import java.util.*;

final class RequestTools {
	private RequestTools() {
		// prevent instantiation
	}

	public static List<Integer> convertDates(final Calendar... dates) {
		return RequestTools.convertDates(Arrays.asList(dates));
	}

	public static List<Integer> convertDates(final Iterable<Calendar> dates) {
		List<Integer> result = new ArrayList<Integer>();
		for (Calendar date: dates) {
			result.add(Integer.parseInt(String.format("%1$tY%1$tm%1$td", date)));
		}
		return result;
	}
}
