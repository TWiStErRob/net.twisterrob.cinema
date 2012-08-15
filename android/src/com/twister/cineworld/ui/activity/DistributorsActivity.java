package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldDistributor;
import com.twister.cineworld.ui.adapter.DistributorAdapter;

public class DistributorsActivity extends BaseListActivity<CineworldDistributor> {
	public DistributorsActivity() {
		super(R.layout.activity_distributors, R.menu.context_item_distributor);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldDistributor item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	public List<CineworldDistributor> doWork() {
		return new CineworldAccessor().getAllDistributors();
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldDistributor> result) {
		return new DistributorAdapter(DistributorsActivity.this, result);
	}

}
