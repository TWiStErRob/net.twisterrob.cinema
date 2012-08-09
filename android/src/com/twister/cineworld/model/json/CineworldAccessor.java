package com.twister.cineworld.model.json;

import java.io.IOException;
import java.util.*;

import android.util.Log;

import com.twister.cineworld.ui.Tools;

public class CineworldAccessor {
	public static final String	DEVELOPER_KEY	= "9qfgpF7B";
	public static final String	BASE_URI		= "http://www.cineworld.co.uk/api/quickbook";
	public static final String	URI_FILMS_ALL	= CineworldAccessor.makeUri("films", true);

	public List<CineworldFilm> getAllFilms() {
		List<CineworldFilm> result = Collections.emptyList();
		try {
			result = new CineworldJsonClient().get(CineworldAccessor.URI_FILMS_ALL, FilmsResponse.class).films;
		} catch (IOException ex) {
			Log.d("ACCESS", "Unable to get all films", ex);
			Tools.toast(ex.getMessage()); // TODO
		}
		return result;
	}

	private static String makeUri(final String requestType, final boolean full, final String... filters) {
		StringBuilder filterString = new StringBuilder();
		for (String filter : filters) {
			filterString.append("&" + filter);
		}
		return String.format("%s/%s?key=%s&full=%s&%s", CineworldAccessor.BASE_URI, requestType, CineworldAccessor.DEVELOPER_KEY, full, filterString);
	}
}
