package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.MovieMatcher;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.ui.adapter.FilmAdapter;

public class FilmsActivity extends BaseListActivity<MovieBase> {
	private FilmsUIRequest	m_request;

	public FilmsActivity() {
		super(R.layout.activity_list, R.menu.context_item_film);
		super.setAutoLoad(false);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new FilmsUIRequest(getIntent());
		startLoad();
		setTitle(m_request.getTitle(getResources()));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final MovieBase item) {
		menu.setHeaderTitle(item.getTitle());
		menu.findItem(R.id.menuitem_film_details_2d).setVisible(item.has2D());
		menu.findItem(R.id.menuitem_film_when_2d).setVisible(item.has2D());
		menu.findItem(R.id.menuitem_film_where_2d).setVisible(item.has2D());
		menu.findItem(R.id.menuitem_film_details_3d).setVisible(item.has3D());
		menu.findItem(R.id.menuitem_film_when_3d).setVisible(item.has3D());
		menu.findItem(R.id.menuitem_film_where_3d).setVisible(item.has3D());
		menu.findItem(R.id.menuitem_film_details_2d_imax).setVisible(item.hasIMax2D());
		menu.findItem(R.id.menuitem_film_when_2d_imax).setVisible(item.hasIMax2D());
		menu.findItem(R.id.menuitem_film_where_2d_imax).setVisible(item.hasIMax2D());
		menu.findItem(R.id.menuitem_film_details_3d_imax).setVisible(item.hasIMax3D());
		menu.findItem(R.id.menuitem_film_when_3d_imax).setVisible(item.hasIMax3D());
		menu.findItem(R.id.menuitem_film_where_3d_imax).setVisible(item.hasIMax3D());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final MovieBase item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_film_details_2d:
			case R.id.menuitem_film_details_3d:
			case R.id.menuitem_film_details_2d_imax:
			case R.id.menuitem_film_details_3d_imax: {
				Film film = getFilm(item, menu.getItemId(),
						R.id.menuitem_film_details_2d, R.id.menuitem_film_details_2d_imax,
						R.id.menuitem_film_details_3d, R.id.menuitem_film_details_3d_imax);
				Intent intent = new Intent(getApplicationContext(), FilmActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_FILM, film);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_film_when_2d:
			case R.id.menuitem_film_when_3d:
			case R.id.menuitem_film_when_2d_imax:
			case R.id.menuitem_film_when_3d_imax: {
				Film film = getFilm(item, menu.getItemId(),
						R.id.menuitem_film_when_2d, R.id.menuitem_film_when_2d_imax,
						R.id.menuitem_film_when_3d, R.id.menuitem_film_when_3d_imax);
				Intent intent = new Intent(getApplicationContext(), DatesActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_FILM, film);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_film_where_2d:
			case R.id.menuitem_film_where_3d:
			case R.id.menuitem_film_where_2d_imax:
			case R.id.menuitem_film_where_3d_imax: {
				Film film = getFilm(item, menu.getItemId(),
						R.id.menuitem_film_where_2d, R.id.menuitem_film_where_2d_imax,
						R.id.menuitem_film_where_3d, R.id.menuitem_film_where_3d_imax);
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_FILM, film);
				this.startActivity(intent);
				return true;
			}
			default: {
				return super.onContextItemSelected(menu, item);
			}
		}
	}

	private Film getFilm(final MovieBase item, final int itemId,
			final int id2D, final int id2dIMax, final int id3D, final int id3dIMax) {
		if (item instanceof Movie) {
			Movie film = (Movie) item;
			if (itemId == id2D) {
				return film.get2D();
			} else if (itemId == id3D) {
				return film.get3D();
			} else if (itemId == id2dIMax) {
				return film.getIMax2D();
			} else if (itemId == id3dIMax) {
				return film.getIMax3D();
			}
		}
		return null;
	}

	@Override
	public List<MovieBase> loadList() throws ApplicationException {
		List<Film> list = m_request.getList();
		MovieMatcher matcher = new MovieMatcher();
		List<Movie> films = matcher.match(list);
		List<MovieBase> seriesAndFilms = matcher.matchSeries(films);
		return seriesAndFilms;
	}

	@Override
	protected ListAdapter createAdapter(final List<MovieBase> result) {
		return new FilmAdapter(FilmsActivity.this, result);
	}
}
