package com.twister.cineworld.tools;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.apache.http.*;

import android.graphics.*;
import android.graphics.drawable.*;

public class IOTools {
	private IOTools() {
		// prevent instantiation
	}

	public static String readAll(final Reader r) {
		StringBuilder sb = new StringBuilder();
		try {
			int c = 0;
			while (c != -1) {
				c = r.read();
				sb.append((char) c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static String getEncoding(final HttpEntity entity) {
		String encoding = Charset.defaultCharset().name();
		Header header = entity.getContentEncoding();
		if (header != null) {
			encoding = header.getValue();
		} else {
			header = entity.getContentType();
			if (header != null) {
				String value = header.getValue();
				int index = value.indexOf("charset=") + 8;
				int endIndex = value.indexOf(';', index);
				if (endIndex == -1) {
					endIndex = value.length();
				}
				encoding = value.substring(index, endIndex);
			}
		}
		return encoding;
	}

	public static Drawable getImage(final String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();

		Bitmap bitmap = BitmapFactory.decodeStream(input);
		connection.disconnect();
		return new BitmapDrawable(bitmap);
	}
}
