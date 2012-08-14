package com.twister.cineworld.model.json.request;

import java.net.*;
import java.util.Arrays;

import android.util.Log;

import com.twister.cineworld.model.json.data.CineworldBase;
import com.twister.cineworld.model.json.response.BaseListResponse;

public abstract class BaseListRequest<T extends CineworldBase> {
	public static final String		DEFAULT_DEVELOPER_KEY	= "9qfgpF7B";
	public static final String		DEFAULT_TERRITORY		= "GB";								// TODO get from config
	public static final String		DEFAULT_CALLBACK		= null;								// no need for android

	private static final String		BASE_URL_STRING			= "http://www.cineworld.co.uk/api/";
	private static final URL		BASE_URL;
	static {
		try {
			BASE_URL = new URL(BaseListRequest.BASE_URL_STRING);
		} catch (MalformedURLException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	protected static final String	KEY_KEY					= "key";
	protected static final String	KEY_TERRITORY			= "territory";
	protected static final String	KEY_CALLBACK			= "callback";
	protected static final String	KEY_FULL				= "full";
	protected static final String	KEY_FILM				= "film";
	protected static final String	KEY_DATE				= "date";
	protected static final String	KEY_CINEMA				= "cinema";
	protected static final String	KEY_CATEGORY			= "category";
	protected static final String	KEY_EVENT				= "event";
	protected static final String	KEY_DISTRIBUTOR			= "distributor";

	private String					m_key					= BaseListRequest.DEFAULT_DEVELOPER_KEY;
	private String					m_territory				= BaseListRequest.DEFAULT_TERRITORY;
	private String					m_callback				= BaseListRequest.DEFAULT_CALLBACK;

	public BaseListRequest() {
	}

	/**
	 * @param territory (Optional, default GB) Sets which territory to return cinemas for, valid values for United Kingdom and Ireland are; GB and IE.
	 * @param callback (Optional, no default) Wraps the response JSON in the callback function specified to allow cross browser scripting, note that if you use
	 *        jQuery and JSONP the callback parameter is automatically added for you.
	 */
	public BaseListRequest(final String territory, final String callback) {
		m_territory = territory;
		m_callback = callback;
	}

	/**
	 * @param key (Required) Your developer API key.
	 * @param territory (Optional, default GB) Sets which territory to return cinemas for, valid values for United Kingdom and Ireland are; GB and IE.
	 * @param callback (Optional, no default)Wraps the response JSON in the callback function specified to allow cross browser scripting, note that if you use
	 *        jQuery and JSONP the callback parameter is automatically added for you.
	 */
	public BaseListRequest(final String key, final String territory, final String callback) {
		m_key = key;
		m_territory = territory;
		m_callback = callback;
	}

	/**
	 * @required
	 * @return Your developer API key.
	 */
	public String getKey() {
		return m_key;
	}

	/**
	 * @required
	 * @param key Your developer API key.
	 */
	public void setKey(final String key) {
		m_key = key;
	}

	/**
	 * @optional GB
	 * @return Sets which territory to return cinemas for, valid values for United Kingdom and Ireland are; GB and IE.
	 */
	public String getTerritory() {
		return m_territory;
	}

	/**
	 * @optional GB
	 * @param territory Sets which territory to return cinemas for, valid values for United Kingdom and Ireland are; GB and IE.
	 */
	public void setTerritory(final String territory) {
		m_territory = territory;
	}

	/**
	 * @optional N/A
	 * @return Wraps the response JSON in the callback function specified to allow cross browser scripting, note that if you use jQuery and JSONP the callback
	 *         parameter is automatically added for you.
	 */
	public String getCallback() {
		return m_callback;
	}

	/**
	 * @optional N/A
	 * @param callback Wraps the response JSON in the callback function specified to allow cross browser scripting, note that if you use jQuery and JSONP the
	 *        callback parameter is automatically added for you.
	 */
	public void setCallback(final String callback) {
		m_callback = callback;
	}

	/**
	 * Generate the full URL to make this request.
	 * 
	 * @return request URL
	 */
	public abstract URL getURL();

	/**
	 * Return the request type of this request, for display purposes; but return an identifier-like {@link String}.
	 * 
	 * @return request type
	 */
	public abstract String getRequestType();

	/**
	 * Expected response {@link Class}.
	 * 
	 * @return response {@link Class}
	 */
	public abstract Class<? extends BaseListResponse<T>> getResponseClass();

	protected static URL makeUrl(final String requestType, final Object... filters) {
		StringBuilder filterString = new StringBuilder();
		assert filters.length % 2 == 0;
		for (int i = 0; i < filters.length; i += 2) {
			if (filters[i + 1] != null) {
				filterString.append('&');
				filterString.append(filters[i]);
				filterString.append('=');
				filterString.append(filters[i + 1]);
			}
		}
		String spec = requestType;
		try {
			spec = String.format("%s?key=%s%s", spec, BaseListRequest.DEFAULT_DEVELOPER_KEY, filterString);
			return new URL(BaseListRequest.BASE_URL, spec);
		} catch (MalformedURLException ex) {
			Log.e("ACCESS", String.format("Cannot initialize urls: %s (%s -> %s)", requestType, Arrays.toString(filters), spec, ex));
			return BaseListRequest.BASE_URL; // TODO throw
		}
	}

	public static URL makeUrl(final String requestType, final String... filters) {
		StringBuilder filterString = new StringBuilder();
		for (String filter : filters) {
			filterString.append("&" + filter);
		}

		try {
			String spec = String.format("%s?key=%s%s", requestType, BaseListRequest.DEFAULT_DEVELOPER_KEY, filterString);
			return new URL(BaseListRequest.BASE_URL, spec);
		} catch (MalformedURLException ex) {
			Log.e("ACCESS", String.format("Cannot initialize urls: %s (%s)", requestType, Arrays.toString(filters), ex));
			return BaseListRequest.BASE_URL; // TODO throw
		}
	}
}
