package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldDistributor;
import com.twister.cineworld.ui.adapter.DistributorAdapter;

public class DistributorsActivity extends BaseListActivity<CineworldDistributor, CineworldDistributor> {
	public DistributorsActivity() {
		super(R.layout.activity_distributors, R.menu.context_item_distributor);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldDistributor item) {
		menu.setHeaderTitle(item.getName());
	}

	public List<CineworldDistributor> retrieve() {
		return new CineworldAccessor().getAllDistributors();
	}

	public List<CineworldDistributor> process(final List<CineworldDistributor> list) {
		return list;
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldDistributor> result) {
		return new DistributorAdapter(DistributorsActivity.this, result);
	}
}
