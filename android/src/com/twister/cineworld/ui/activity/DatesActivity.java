package com.twister.cineworld.ui.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldDate;
import com.twister.cineworld.ui.Tools;
import com.twister.cineworld.ui.adapter.DateAdapter;

public class DatesActivity extends Activity {
	private AbsListView	m_listView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Tools.s_context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cinemas);

		m_listView = (AbsListView) findViewById(android.R.id.list);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			@Override
			public void run() {
				Tools.toast("Requesting");
				final List<CineworldDate> result = new CineworldAccessor().getAllDates();
				DatesActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						m_listView.setAdapter(new DateAdapter(DatesActivity.this, result));
					}
				});
				Tools.toast("Got " + result.size());
			}
		}.start();
	}
}
