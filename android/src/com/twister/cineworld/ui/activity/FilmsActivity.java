package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.MovieMatcher;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.ui.adapter.FilmAdapter;

public class FilmsActivity extends BaseListActivity<MovieBase> {
	/**
	 * Cinema ID<br>
	 * <b>Type</b>: Integer
	 */
	public static final String	EXTRA_CINEMA_ID			= "cinema_id";
	/**
	 * Distributor ID<br>
	 * <b>Type</b>: Integer
	 */
	public static final String	EXTRA_DISTRIBUTOR_ID	= "distributor_id";
	private Integer				m_cinemaId;
	private Integer				m_distributorId;

	public FilmsActivity() {
		super(R.layout.activity_list, R.menu.context_item_film);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_cinemaId = getExtra(FilmsActivity.EXTRA_CINEMA_ID);
		m_distributorId = getExtra(FilmsActivity.EXTRA_DISTRIBUTOR_ID);
		setTitle(getResources().getString(R.string.title_activity_films_loading, m_cinemaId));
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
				intent.putExtra(FilmActivity.EXTRA_EDI, film.getEdi());
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
				intent.putExtra(DatesActivity.EXTRA_EDI, film.getEdi());
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
				intent.putExtra(CinemasActivity.EXTRA_FILM, film);
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
		List<com.twister.cineworld.model.generic.Film> list;
		if (m_cinemaId != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForCinema(m_cinemaId);
		} else if (m_distributorId != null) {
			list = App.getInstance().getCineworldAccessor().getFilmsForDistributor(m_distributorId);
		} else {
			list = App.getInstance().getCineworldAccessor().getAllFilms();
		}

		// transform
		MovieMatcher matcher = new MovieMatcher();
		List<Movie> films = matcher.match(list);
		List<MovieBase> seriesAndFilms = matcher.matchSeries(films);
		return seriesAndFilms;
	}

	@Override
	protected ListAdapter createAdapter(final List<MovieBase> result) {
		setTitle(getResources().getString(R.string.title_activity_films_loaded, m_cinemaId)); // TODO move
		return new FilmAdapter(FilmsActivity.this, result);
	}
}
