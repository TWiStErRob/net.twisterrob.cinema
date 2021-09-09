package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Cinema;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasActivity extends BaseListActivity<Cinema> {
	private CinemasUIRequest	m_request;

	public CinemasActivity() {
		super(R.layout.activity_list);
		super.setAutoLoad(false);
		super.setContextMenu(R.menu.context_item_cinema);
		super.setOptionsMenu(R.menu.activity_cinemas);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new CinemasUIRequest(this.getIntent());
		startLoad();
		setTitle(m_request.getTitle(getResources()));
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuitem_cinemas_asMap:
				Intent intent = new Intent(getApplicationContext(), CinemasMapActivity.class);
				intent.putExtras(getIntent());
				this.startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
				intent.putExtra(UIRequestExtras.EXTRA_CINEMA, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_cinema_films: {
				Intent intent = new Intent(getApplicationContext(), FilmsActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_CINEMA, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_cinema_dates: {
				Intent intent = new Intent(getApplicationContext(), DatesActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_CINEMA, item);
				this.startActivity(intent);
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}

	@Override
	protected List<Cinema> loadList() throws ApplicationException {
		return m_request.getList();
	}

	@Override
	protected ListAdapter createAdapter(final List<Cinema> result) {
		return new CinemaAdapter(this, result, null);
	}
}
