package com.twister.cineworld.model.json;

import java.net.URL;

import com.twister.cineworld.exception.*;

public interface JsonClient {
	/**
	 * Converts received JSON into passed type
	 * 
	 * @param url address to which are we posting
	 * @param requestObject post data
	 * @param responseType type of the response we are expecting
	 * @return instance of the class
	 * @throws NetworkException
	 * @throws ExternalException
	 * @throws InternalException
	 */
	public abstract <T> T get(URL url, Class<T> responseType) throws ApplicationException;

	/**
	 * Converts received JSON into passed type
	 * 
	 * @param url address to which are we posting
	 * @param requestObject post data
	 * @param responseType type of the response we are expecting
	 * @return instance of the class
	 * @throws NetworkException
	 * @throws ExternalException
	 * @throws InternalException
	 */
	public abstract <T> T post(String url, Object requestObject, Class<T> responseType) throws ApplicationException;
}
