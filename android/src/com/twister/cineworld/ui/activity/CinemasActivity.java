package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.*;

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
		return super.onContextItemSelected(menu);
	}

	public List<CineworldCinema> getList() {
		List<CineworldCinema> result = new CineworldAccessor().getAllCinemas();
		return result;
	}

	public List<CineworldCinema> postProcess(final List<CineworldCinema> list) {
		return list;
	}

	public void updateUI(final List<CineworldCinema> result) {
		getListView().setAdapter(new CinemaAdapter(this, result));
	}

}
