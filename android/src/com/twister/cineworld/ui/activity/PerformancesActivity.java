package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Performance;
import com.twister.cineworld.ui.adapter.PeformanceAdapter;

public class PerformancesActivity extends BaseListActivity<Performance> {
	public PerformancesActivity() {
		super(R.layout.activity_list, R.menu.context_item_performance);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Performance item) {
		menu.setHeaderTitle(item.getTime());
	}

	@Override
	public List<Performance> loadList() throws ApplicationException {
		return App.getInstance().getCineworldAccessor().getPeformances(66, 62278, 20130427);
	}

	@Override
	protected ListAdapter createAdapter(final List<Performance> result) {
		return new PeformanceAdapter(PerformancesActivity.this, result);
	}
}
