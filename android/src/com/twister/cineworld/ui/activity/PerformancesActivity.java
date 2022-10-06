package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Performance;
import com.twister.cineworld.ui.adapter.PeformanceAdapter;

public class PerformancesActivity extends BaseListActivity<Performance> {
	private PerformancesUIRequest	m_request;

	public PerformancesActivity() {
		super(R.layout.activity_list);
		super.setContextMenu(R.menu.context_item_performance);
		super.setAutoLoad(false);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new PerformancesUIRequest(getIntent());
		startLoad();
		setTitle(m_request.getTitle(getResources()));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Performance item) {
		menu.setHeaderTitle(item.getTime());
	}

	@Override
	public List<Performance> loadList() throws ApplicationException {
		return m_request.getList();
	}

	@Override
	protected ListAdapter createAdapter(final List<Performance> result) {
		return new PeformanceAdapter(PerformancesActivity.this, result);
	}
}
