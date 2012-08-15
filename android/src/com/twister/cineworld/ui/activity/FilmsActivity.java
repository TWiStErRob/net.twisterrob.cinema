package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.*;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.cineworld.ui.*;
import com.twister.cineworld.ui.adapter.FilmAdapter;

public class FilmsActivity extends BaseListActivity<FilmBase> {
	/**
	 * Cinema ID<br>
	 * <b>Type</b>: Integer
	 */
	public static final String	EXTRA_CINEMA_ID	= "ciname_id";
	private Integer	           m_cinemaId;

	public FilmsActivity() {
		super(R.layout.activity_films, R.menu.context_item_film);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_cinemaId = getExtra(FilmsActivity.EXTRA_CINEMA_ID);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final FilmBase item) {
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
	protected boolean onContextItemSelected(final MenuItem menu, final FilmBase item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_film_details_2d:
			case R.id.menuitem_film_details_3d:
			case R.id.menuitem_film_details_2d_imax:
			case R.id.menuitem_film_details_3d_imax: {
				CineworldFilm film = getFilm(item, menu.getItemId(),
				        R.id.menuitem_film_details_2d, R.id.menuitem_film_details_2d_imax,
				        R.id.menuitem_film_details_3d, R.id.menuitem_film_details_3d_imax);
				Tools.toast("TODO implement film details of " + film.getEdi());
				// Intent intent = new Intent(getApplicationContext(), FilmActivity.class);
				// intent.putExtra(FilmActivity.EXTRA_EDI, film.getEdi());
				// this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_film_when_2d:
			case R.id.menuitem_film_when_3d:
			case R.id.menuitem_film_when_2d_imax:
			case R.id.menuitem_film_when_3d_imax: {
				CineworldFilm film = getFilm(item, menu.getItemId(),
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
				CineworldFilm film = getFilm(item, menu.getItemId(),
				        R.id.menuitem_film_where_2d, R.id.menuitem_film_where_2d_imax,
				        R.id.menuitem_film_where_3d, R.id.menuitem_film_where_3d_imax);
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(CinemasActivity.EXTRA_EDI, film.getEdi());
				this.startActivity(intent);
				return true;
			}
			default: {
				return super.onContextItemSelected(menu, item);
			}
		}
	}

	private CineworldFilm getFilm(final FilmBase item, final int itemId,
	        final int id2D, final int id2dIMax, final int id3D, final int id3dIMax) {
		if (item instanceof Film) {
			Film film = (Film) item;
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
	public List<FilmBase> doWork() {
		List<CineworldFilm> list;
		if (m_cinemaId != null) {
			list = new CineworldAccessor().getFilms(m_cinemaId);
		} else {
			list = new CineworldAccessor().getAllFilms();
		}

		// transform
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
