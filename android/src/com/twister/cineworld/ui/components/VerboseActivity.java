package com.twister.cineworld.ui.components;

import android.app.Activity;
import android.os.Bundle;

import com.twister.cineworld.log.*;

public class VerboseActivity extends Activity {
	private static final Log	LOG	= LogFactory.getLog(Tag.UI);

	public VerboseActivity() {
		super();
		log("ctor()");
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate(savedInstanceState)");
	}

	@Override
	protected void onStart() {
		super.onStart();
		log("onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		log("onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		log("onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		log("onStop()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		log("onRestart()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log("onDestroy()");
	}

	private void log(final String method) {
		LOG.verbose("%1$s@%3$08X.%2$s", getClass().getSimpleName(), method, hashCode());
	}
}
