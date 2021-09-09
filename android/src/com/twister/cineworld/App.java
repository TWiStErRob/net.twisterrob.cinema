package com.twister.cineworld;

import java.net.URL;
import java.util.*;

import android.graphics.Bitmap;

import com.twister.cineworld.db.DataBaseHelper;
import com.twister.cineworld.model.accessor.Accessor;
import com.twister.cineworld.model.accessor.impl.*;
import com.twister.cineworld.model.generic.Location;
import com.twister.cineworld.tools.StringTools;
import com.twister.cineworld.tools.cache.*;
import com.twister.cineworld.ui.ProgressReporter;

public class App extends android.app.Application {
	private static/* final */App						s_instance;
	private static final String						CACHE_GEO	= GeoDBCache.class.getName();
	private static final String						CACHE_IMAGE	= ImageSDNetCache.class.getName();
	private static final Map<String, Cache<?, ?>>	s_caches	= new HashMap<String, Cache<?, ?>>();

	public App() {
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	private DataBaseHelper		m_dataBaseHelper	= new DataBaseHelper(this);
	private ProgressReporter	m_status;
	private static List<String>	s_logList			= new LinkedList<String>();

	public static void exit() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public DataBaseHelper getDataBaseHelper() {
		return m_dataBaseHelper;
	}

	public Accessor getCineworldAccessor() {
		// return new CineworldJSONAccessor();
		return new TrivialDBCacheAccessor(new DBAccessor(), new CineworldJSONAccessor());
	}

	public Cache<String, Location> getGeoCache() {
		return getCache(CACHE_GEO);
	}

	public Cache<URL, Bitmap> getPosterCache() {
		return getCache(CACHE_IMAGE);
	}

	private <K, V> Cache<K, V> getCache(final String cacheName) {
		@SuppressWarnings("unchecked")
		Cache<K, V> cache = (Cache<K, V>) s_caches.get(cacheName);
		if (cache == null) {
			cache = createCache(cacheName);
			s_caches.put(cacheName, cache);
		}
		return cache;
	}

	@SuppressWarnings("unchecked")
	private <T> T createCache(final String cacheClass) {
		try {
			return (T) Class.forName(cacheClass).newInstance();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void setActiveStatusBar(final ProgressReporter status) {
		m_status = status;
	}

	public static void reportStatus(final String messageFormat, final Object... messageArgs) {
		String message = StringTools.format(messageFormat, messageArgs);
		ProgressReporter bar = App.getInstance().m_status;
		if (bar != null) {
			synchronized (s_logList) {
				s_logList.add(String.format("%tT: %s", Calendar.getInstance(), message));
				while (s_logList.size() > 5) {
					s_logList.remove(0);
				}
				message = StringTools.join(s_logList, "\n");
			}
			bar.reportStatus(message);
		}
	}
}
