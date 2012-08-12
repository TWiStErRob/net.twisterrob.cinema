package com.twister.cineworld.ui;

import java.util.List;

import android.app.Activity;

public class AsyncRetrieverExecutor {
	private Activity	m_context;

	public AsyncRetrieverExecutor(final Activity context) {
		m_context = context;
	}

	public <RawItem, UIItem> void execute(final Retriever<RawItem, UIItem> retriever) {
		new Thread() {
			@Override
			public void run() {
				Tools.toast("Requesting");
				List<RawItem> list = retriever.retrieve();
				final List<UIItem> result = retriever.process(list);
				m_context.runOnUiThread(new Runnable() {
					public void run() {
						retriever.update(result);
					}
				});
				Tools.toast("Got " + result.size());
			}
		}.start();
	}
}
