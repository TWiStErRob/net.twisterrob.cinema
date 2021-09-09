package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Distributor;
import com.twister.cineworld.ui.adapter.DistributorAdapter;

public class DistributorsActivity extends BaseListActivity<Distributor> {
	public DistributorsActivity() {
		super(R.layout.activity_list);
		super.setContextMenu(R.menu.context_item_distributor);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getResources().getString(R.string.title_activity_distributors_all));
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

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Distributor item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_distributor_cinemas: {
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_DISTRIBUTOR, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_distributor_films: {
				Intent intent = new Intent(getApplicationContext(), FilmsActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_DISTRIBUTOR, item);
				this.startActivity(intent);
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}
}
