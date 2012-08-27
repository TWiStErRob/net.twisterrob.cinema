package com.twister.cineworld.log;

import java.util.concurrent.atomic.AtomicReferenceArray;

public final class LogFactory {

	private static final AtomicReferenceArray<CineworldLogger>	LOGS	= new AtomicReferenceArray<CineworldLogger>(
																				Tag.values().length);

	public static CineworldLogger getLog(final Tag tag) {
		CineworldLogger ret = LogFactory.LOGS.get(tag.ordinal());
		if (ret == null) {
			synchronized (LogFactory.LOGS) {
				ret = LogFactory.LOGS.get(tag.ordinal());
				if (ret == null) {
					ret = new CineworldLogger(tag);
					LogFactory.LOGS.set(tag.ordinal(), ret);
				}
			}
		}
		return ret;
	}

	private LogFactory() {
		// factory class
	}

}
