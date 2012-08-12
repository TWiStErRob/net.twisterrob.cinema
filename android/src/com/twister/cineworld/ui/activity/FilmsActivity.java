package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.*;

import com.twister.cineworld.R;
import com.twister.cineworld.model.*;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.cineworld.ui.*;
import com.twister.cineworld.ui.adapter.FilmAdapter;

public class FilmsActivity extends BaseListActivity<FilmBase> {
	public FilmsActivity() {
		super(R.layout.activity_films, R.menu.context_item_film);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			@Override
			public void run() {
				Tools.toast("Requesting");
				List<CineworldFilm> cineFilms = new CineworldAccessor().getAllFilms();
				FilmMatcher matcher = new FilmMatcher();
				List<Film> films = matcher.match(cineFilms);
				final List<FilmBase> filmList = matcher.matchSeries(films);
				FilmsActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						getListView().setAdapter(new FilmAdapter(FilmsActivity.this, filmList));
					}
				});
				Tools.toast("Got " + films.size() + " / " + cineFilms.size());
			}
		}.start();
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final FilmBase filmBase) {
		menu.setHeaderTitle(filmBase.getTitle());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem item, final FilmBase filmBase) {
		switch (item.getItemId()) {
			case R.id.menuitem_film_details:
				// TODO
				return true;
			case R.id.menuitem_film_when:
				// TODO
				return true;
			case R.id.menuitem_film_where:
				// TODO
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
}
