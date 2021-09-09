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
		super(R.layout.activity_list);
		super.setContextMenu(R.menu.context_item_film);
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

		menu.setGroupVisible(R.id.menuitemgroup_film_2d, item.has2D());
		menu.setGroupVisible(R.id.menuitemgroup_film_3d, item.has3D());
		menu.setGroupVisible(R.id.menuitemgroup_film_2d_imax, item.hasIMax2D());
		menu.setGroupVisible(R.id.menuitemgroup_film_3d_imax, item.hasIMax3D());

		boolean hasCinema = m_request.getCinema() != null;
		Object[] args = !hasCinema? null : new Object[] { m_request.getCinema().getName() };
		formatMenuAndVisible(menu, R.id.menuitem_film_when_cinema_2d, hasCinema, args);
		formatMenuAndVisible(menu, R.id.menuitem_film_when_cinema_3d, hasCinema, args);
		formatMenuAndVisible(menu, R.id.menuitem_film_when_cinema_2d_imax, hasCinema, args);
		formatMenuAndVisible(menu, R.id.menuitem_film_when_cinema_3d_imax, hasCinema, args);
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final MovieBase item) {
		int itemId = menu.getItemId();
		switch (itemId) {
			case R.id.menuitem_film_details_2d:
			case R.id.menuitem_film_details_3d:
			case R.id.menuitem_film_details_2d_imax:
			case R.id.menuitem_film_details_3d_imax: {
				Film film = getFilm(item, itemId,
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
				Film film = getFilm(item, itemId,
						R.id.menuitem_film_when_2d, R.id.menuitem_film_when_2d_imax,
						R.id.menuitem_film_when_3d, R.id.menuitem_film_when_3d_imax);
				Intent intent = new Intent(getApplicationContext(), DatesActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_FILM, film);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_film_when_cinema_2d:
			case R.id.menuitem_film_when_cinema_3d:
			case R.id.menuitem_film_when_cinema_2d_imax:
			case R.id.menuitem_film_when_cinema_3d_imax: {
				Film film = getFilm(item, itemId,
						R.id.menuitem_film_when_cinema_2d, R.id.menuitem_film_when_cinema_2d_imax,
						R.id.menuitem_film_when_cinema_3d, R.id.menuitem_film_when_cinema_3d_imax);
				Intent intent = new Intent(getApplicationContext(), DatesActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_CINEMA, m_request.getCinema());
				intent.putExtra(UIRequestExtras.EXTRA_FILM, film);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_film_where_2d:
			case R.id.menuitem_film_where_3d:
			case R.id.menuitem_film_where_2d_imax:
			case R.id.menuitem_film_where_3d_imax: {
				Film film = getFilm(item, itemId,
						R.id.menuitem_film_where_2d, R.id.menuitem_film_where_2d_imax,
						R.id.menuitem_film_where_3d, R.id.menuitem_film_where_3d_imax);
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_FILM, film);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_film_where_2d_map:
			case R.id.menuitem_film_where_3d_map:
			case R.id.menuitem_film_where_2d_imax_map:
			case R.id.menuitem_film_where_3d_imax_map: {
				Film film = getFilm(item, itemId,
						R.id.menuitem_film_where_2d_map, R.id.menuitem_film_where_2d_imax_map,
						R.id.menuitem_film_where_3d_map, R.id.menuitem_film_where_3d_imax_map);
				Intent intent = new Intent(getApplicationContext(), CinemasMapActivity.class);
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
