package com.twister.cineworld.model.json;

import java.util.List;

import android.app.Activity;

import com.twister.cineworld.ui.Tools;

public class AsyncAccessorExecutor {
	private Activity	m_context;

	public AsyncAccessorExecutor(final Activity context) {
		m_context = context;
	}

	public <RawResponse, UIResponse> void execute(final AsyncAccessor<RawResponse, UIResponse> accessor) {
		new Thread() {
			@Override
			public void run() {
				Tools.toast("Requesting");
				List<RawResponse> list = accessor.getList();
				final List<UIResponse> result = accessor.postProcess(list);
				m_context.runOnUiThread(new Runnable() {
					public void run() {
						accessor.updateUI(result);
					}

				});
				Tools.toast("Got " + result.size());
			}
		}.start();
	}
}