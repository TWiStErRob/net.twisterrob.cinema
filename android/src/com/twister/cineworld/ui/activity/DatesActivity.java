package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldDate;
import com.twister.cineworld.ui.adapter.DateAdapter;

public class DatesActivity extends BaseListActivity<CineworldDate> {
	public DatesActivity() {
		super(R.layout.activity_dates, R.menu.context_item_date);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldDate item) {
		menu.setHeaderTitle(item.getDate());
	}

	@Override
	public List<CineworldDate> doWork() {
		return new CineworldAccessor().getAllDates();
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldDate> result) {
		return new DateAdapter(DatesActivity.this, result);
	}
}
