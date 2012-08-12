package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.*;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.cineworld.ui.FilmMatcher;
import com.twister.cineworld.ui.adapter.FilmAdapter;

public class FilmsActivity extends BaseListActivity<CineworldFilm, FilmBase> {
	public FilmsActivity() {
		super(R.layout.activity_films, R.menu.context_item_film);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final FilmBase item) {
		menu.setHeaderTitle(item.getTitle());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final FilmBase item) {
		switch (menu.getItemId()) {
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
				return super.onContextItemSelected(menu, item);
		}
	}

	public List<CineworldFilm> retrieve() {
		return new CineworldAccessor().getAllFilms();
	}

	public List<FilmBase> process(final List<CineworldFilm> list) {
		FilmMatcher matcher = new FilmMatcher();
		List<Film> films = matcher.match(list);
		List<FilmBase> seriesAndFilms = matcher.matchSeries(films);
		return seriesAndFilms;
	}

	@Override
	protected ListAdapter createAdapter(final List<FilmBase> result) {
		return new FilmAdapter(FilmsActivity.this, result);
	}
}
