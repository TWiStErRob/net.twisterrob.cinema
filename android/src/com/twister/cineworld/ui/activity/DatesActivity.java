package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Date;
import com.twister.cineworld.ui.adapter.DateAdapter;

public class DatesActivity extends BaseListActivity<Date> {
	private DatesUIRequest	m_request;

	public DatesActivity() {
		super(R.layout.activity_list);
		super.setContextMenu(R.menu.context_item_date);
		super.setAutoLoad(false);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new DatesUIRequest(getIntent());
		startLoad();
		setTitle(m_request.getTitle(getResources()));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Date item) {
		menu.setHeaderTitle(item.getDate());
	}

	@Override
	public List<Date> loadList() throws ApplicationException {
		return m_request.getList();
	}

	@Override
	protected ListAdapter createAdapter(final List<Date> result) {
		return new DateAdapter(DatesActivity.this, result);
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Date item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_date_films: {
				Intent intent = new Intent(getApplicationContext(), FilmsActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_DATE, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_date_cinemas: {
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_DATE, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_date_films_cinemas: {
				Intent intent = new Intent(getApplicationContext(), PerformancesActivity.class);
				intent.putExtra(UIRequestExtras.EXTRA_DATE, item);
				intent.putExtra(UIRequestExtras.EXTRA_CINEMA, m_request.getCinema());
				intent.putExtra(UIRequestExtras.EXTRA_FILM, m_request.getFilm());
				this.startActivity(intent);
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}
}
