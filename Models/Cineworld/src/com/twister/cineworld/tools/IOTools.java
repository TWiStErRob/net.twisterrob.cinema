package com.twister.cineworld.tools;

import java.io.*;
import java.nio.charset.Charset;

public final class IOTools {
	// TOD check if UTF-8 is used by cineworld
	public static final String ENCODING = Charset.defaultCharset().name();
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private IOTools() {
		// prevent instantiation
	}

	public static String readAll(final Reader r) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int c = r.read(); c != -1; c = r.read()) {
			sb.append((char)c);
		}
		return sb.toString();
	}
}
