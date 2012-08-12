package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
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

	public List<CineworldCinema> retrieve() {
		return new CineworldAccessor().getAllCinemas();
	}

	public List<CineworldCinema> process(final List<CineworldCinema> list) {
		return list;
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldCinema> result) {
		return new CinemaAdapter(this, result);
	}
}
