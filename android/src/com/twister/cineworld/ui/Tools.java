package com.twister.cineworld.ui;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Tools {
	public static Context	s_context;

	public static void toast(final String message) {
		Handler h = new Handler(Tools.s_context.getMainLooper());
		h.post(new Runnable() {
			public void run() {
				Toast.makeText(Tools.s_context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
