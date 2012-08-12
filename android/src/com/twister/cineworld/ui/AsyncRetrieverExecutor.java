package com.twister.cineworld.ui;

import java.util.List;

import android.app.Activity;

/**
 * Asynchronous {@link RetrieverExecutor} for processing from UI.
 * 
 * @author papp.robert.s
 * @param <RawItem> The type of items returned by the lower data handling layers
 * @param <UIItem> The type of items handled on the UI
 */
public class AsyncRetrieverExecutor<RawItem, UIItem> implements RetrieverExecutor<RawItem, UIItem> {
	private Activity	m_context;

	public AsyncRetrieverExecutor(final Activity context) {
		m_context = context;
	}

	/**
	 * Executes the retiever methods as the following:
	 * <ol>
	 * <li> {@link Retriever#retrieve()} on a background thread.</li>
	 * <li> {@link Retriever#process(List)} on a background thread.</li>
	 * <li> {@link Retriever#update(List)} on the UI thread of the provided Activity.</li>
	 * </ol>
	 */
	public void execute(final Retriever<RawItem, UIItem> retriever) {
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
