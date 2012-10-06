package com.twister.cineworld.tools;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.apache.http.*;

import android.graphics.*;
import android.graphics.drawable.*;

import com.twister.cineworld.log.*;

public class IOTools {
	private static final CineworldLogger	LOG							= LogFactory.getLog(Tag.IO);
	// TODO check if UTF-8 is used by cineworld
	public static final String				ENCODING					= Charset.defaultCharset().name();
	private static final String				DEFAULT_HTTP_ENCODING		= IOTools.ENCODING;
	private static final String				HTTP_HEADER_CHARSET_PREFIX	= "charset=";
	public static final String				LINE_SEPARATOR				= System.getProperty("line.separator");

	private IOTools() {
		// prevent instantiation
	}

	public static String readAll(final Reader r) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int c = r.read(); c != -1; c = r.read()) {
			sb.append((char) c);
		}
		return sb.toString();
	}

	public static int copyFile(final String sourceFileName, final String destinationFileName) throws IOException {
		File sourceFile = new File(sourceFileName);
		File destinationFile = new File(destinationFileName);
		return IOTools.copyFile(sourceFile, destinationFile);
	}

	public static int copyFile(final File sourceFile, final File destinationFile) throws IOException {
		destinationFile.getParentFile().mkdirs();
		InputStream in = new FileInputStream(sourceFile);
		OutputStream out = new FileOutputStream(destinationFile);
		return IOTools.copyStream(in, out);
	}

	public static int copyStream(final InputStream in, final OutputStream out) throws IOException {
		byte[] buf = new byte[4096];
		int total = 0;
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
			total += len;
		}
		in.close();
		out.close();
		return total;
	}

	public static String getEncoding(final HttpEntity entity) {
		String encoding = IOTools.DEFAULT_HTTP_ENCODING;
		Header header = entity.getContentEncoding();
		if (header != null) {
			return header.getValue();
		}
		// else
		header = entity.getContentType();
		if (header != null) {
			String value = header.getValue();
			int startIndex = value.indexOf(IOTools.HTTP_HEADER_CHARSET_PREFIX);
			if (startIndex != -1) {
				startIndex += IOTools.HTTP_HEADER_CHARSET_PREFIX.length();
				int endIndex = value.indexOf(';', startIndex);
				if (endIndex == -1) {
					endIndex = value.length();
				}
				return value.substring(startIndex, endIndex);
			}
		}
		return encoding;
	}

	public static Drawable getImage(final String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		try {
			connection.connect();
			InputStream input = connection.getInputStream();

			Bitmap bitmap = BitmapFactory.decodeStream(input);
			return new BitmapDrawable(bitmap);
		} finally {
			connection.disconnect();
		}
	}
}
