package com.twister.cineworld.tools.cache;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;

import com.twister.cineworld.App;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.log.*;
import com.twister.cineworld.tools.IOTools;
import com.twister.cineworld.tools.cache.lowlevel.*;
import com.twister.cineworld.tools.cache.lowlevel.ImageCache.ImageCacheParams;

public class ImageSDNetCache implements Cache<URL, Bitmap> {
	private static final Log	LOG	= LogFactory.getLog(Tag.IO);
	private final ImageCache	m_cache;

	public ImageSDNetCache() {
		ImageCacheParams params = new ImageCache.ImageCacheParams(App.getInstance(), "");
		params.initDiskCacheOnCreate = true;
		params.memoryCacheEnabled = false;
		m_cache = new ImageCache(params);
	}

	public Bitmap get(final URL key) throws ApplicationException {
		if (key == null) {
			return null;
		}
		Bitmap bitmap = null;
		bitmap = m_cache.getBitmapFromDiskCache(key.toString());
		if (bitmap == null) {
			bitmap = getImage(key);
			put(key, bitmap);
		}
		return bitmap;
	}

	private Bitmap getImage(final URL key) throws NetworkException {
		try {
			Bitmap bitmap = IOTools.getImage(key);
			return bitmap;
		} catch (IOException ex) {
			NetworkException newEx = new NetworkException("Cannot get image: %s", ex, key);
			LOG.warn(newEx.getMessage(), ex);
			throw newEx;
		}
	}

	public void put(final URL url, final Bitmap image) {
		if (url != null && image != null) {
			m_cache.addBitmapToCache(url.toString(), image);
		}
	}
}
