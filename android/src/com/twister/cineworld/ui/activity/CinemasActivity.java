package com.twister.cineworld.ui.activity;

import java.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.generic.Cinema;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasActivity extends BaseListActivity<Cinema> {
	/**
	 * Film EDI<br>
	 * <b>Type</b>: Integer
	 */
	public static final String	EXTRA_EDI	= "film_edi";
	private Integer				m_edi;

	public CinemasActivity() {
		super(R.layout.activity_cinemas, R.menu.context_item_cinema);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_edi = getExtra(CinemasActivity.EXTRA_EDI);
		setTitle(getResources().getString(R.string.title_activity_cinemas_loading, m_edi));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Cinema item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Cinema item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_cinema_details: {
				Intent intent = new Intent(getApplicationContext(), CinemaActivity.class);
				intent.putExtra(CinemaActivity.EXTRA_ID, item.getId());
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_cinema_films: {
				Intent intent = new Intent(getApplicationContext(), FilmsActivity.class);
				intent.putExtra(FilmsActivity.EXTRA_CINEMA_ID, item.getId());
				this.startActivity(intent);
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}

	@Override
	protected List<Cinema> loadList() throws CineworldException {
		if (m_edi != null) {
			return Collections.emptyList(); // FIXME App.getInstance().getCineworldAccessor().getCinemas(m_edi);
		} else {
			return App.getInstance().getCineworldAccessor().getAllCinemas();
		}
	}

	@Override
	protected ListAdapter createAdapter(final List<Cinema> result) {
		setTitle(getResources().getString(R.string.title_activity_cinemas_loaded, m_edi));
		return new CinemaAdapter(this, result);
	}
}
