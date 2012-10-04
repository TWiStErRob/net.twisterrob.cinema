package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Distributor;
import com.twister.cineworld.ui.adapter.DistributorAdapter;

public class DistributorsActivity extends BaseListActivity<Distributor> {
	public DistributorsActivity() {
		super(R.layout.activity_distributors, R.menu.context_item_distributor);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Distributor item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	public List<Distributor> loadList() throws ApplicationException {
		return App.getInstance().getCineworldAccessor().getAllDistributors();
	}

	@Override
	protected ListAdapter createAdapter(final List<Distributor> result) {
		return new DistributorAdapter(DistributorsActivity.this, result);
	}

}
