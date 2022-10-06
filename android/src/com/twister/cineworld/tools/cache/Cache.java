package com.twister.cineworld.tools.cache;

import com.twister.cineworld.exception.ApplicationException;

public interface Cache<K, V> {
	V get(K key) throws ApplicationException;

	void put(K key, V value) throws ApplicationException;
}
