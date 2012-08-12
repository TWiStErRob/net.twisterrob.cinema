package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.*;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldCinema;
import com.twister.cineworld.ui.Tools;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasActivity extends BaseListActivity<CineworldCinema> {
	public CinemasActivity() {
		super(R.layout.activity_cinemas, R.menu.context_item_cinema);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			@Override
			public void run() {
				Tools.toast("Requesting");
				final List<CineworldCinema> result = new CineworldAccessor().getAllCinemas();
				CinemasActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						getListView().setAdapter(new CinemaAdapter(CinemasActivity.this, result));
					}
				});
				Tools.toast("Got " + result.size());
			}
		}.start();
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldCinema item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final CineworldCinema item) {
		return super.onContextItemSelected(menu);
	}

}
