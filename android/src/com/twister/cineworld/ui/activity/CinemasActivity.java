package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldCinema;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasActivity extends BaseListActivity<CineworldCinema, CineworldCinema> {
	public CinemasActivity() {
		super(R.layout.activity_cinemas, R.menu.context_item_cinema);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldCinema item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final CineworldCinema item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_cinema_details:
				Intent intent = new Intent(getApplicationContext(), CinemaActivity.class);
				intent.putExtra(CinemaActivity.EXTRA_ID, item.getId());
				this.startActivity(intent);
				return true;
			default:
				return super.onContextItemSelected(menu, item);
		}
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldCinema> result) {
		return new CinemaAdapter(this, result);
	}

	@Override
	protected List<CineworldCinema> doWork() {
		return new CineworldAccessor().getAllCinemas();
	}

}
