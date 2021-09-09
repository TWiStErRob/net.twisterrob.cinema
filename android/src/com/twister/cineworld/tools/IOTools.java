package com.twister.cineworld.tools;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.apache.http.*;

import android.graphics.*;

import com.twister.cineworld.log.*;

public final class IOTools {
	private static final Log	LOG							= LogFactory.getLog(Tag.IO);
	// TODO check if UTF-8 is used by cineworld
	public static final String	ENCODING					= Charset.defaultCharset().name();
	private static final String	DEFAULT_HTTP_ENCODING		= ENCODING;
	private static final String	HTTP_HEADER_CHARSET_PREFIX	= "charset=";
	public static final String	LINE_SEPARATOR				= System.getProperty("line.separator");

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
		String encoding = DEFAULT_HTTP_ENCODING;
		Header header = entity.getContentEncoding();
		if (header != null) {
			return header.getValue();
		}
		// else
		header = entity.getContentType();
		if (header != null) {
			String value = header.getValue();
			int startIndex = value.indexOf(HTTP_HEADER_CHARSET_PREFIX);
			if (startIndex != -1) {
				startIndex += HTTP_HEADER_CHARSET_PREFIX.length();
				int endIndex = value.indexOf(';', startIndex);
				if (endIndex == -1) {
					endIndex = value.length();
				}
				return value.substring(startIndex, endIndex);
			}
		}
		return encoding;
	}

	public static Bitmap getImage(final URL url) throws IOException {
		HttpURLConnection connection = IOTools.getWithRedirects(url);
		try {
			InputStream input = connection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			return bitmap;
		} finally {
			connection.disconnect();
		}
	}

	private static HttpURLConnection getWithRedirects(URL url) throws IOException {
		HttpURLConnection connection = null;
		try {
			boolean retry = true;
			int follows = 0;
			do {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				if (connection.getResponseCode() == 301 || connection.getResponseCode() == 302) {
					url = new URL(connection.getHeaderField("Location"));
					connection.disconnect();
					follows++;
				} else {
					retry = false;
				}
			} while (retry && follows <= 5);
			if (retry) {
				throw new IOException("Too many redirects: " + url);
			}
			return connection;
		} catch (IOException ex) {
			if (connection != null) {
				connection.disconnect();
			}
			throw ex;
		} catch (RuntimeException ex) {
			if (connection != null) {
				connection.disconnect();
			}
			throw ex;
		}
	}

	public static Bitmap getImage(final String urlString) throws IOException {
		URL url = new URL(urlString);
		return IOTools.getImage(url);
	}
}
