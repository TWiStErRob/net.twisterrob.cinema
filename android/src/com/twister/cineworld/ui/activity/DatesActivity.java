package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Date;
import com.twister.cineworld.ui.adapter.DateAdapter;

public class DatesActivity extends BaseListActivity<Date> {
	public static final String	EXTRA_EDI	= "film_edi";
	private Integer				m_edi;

	public DatesActivity() {
		super(R.layout.activity_list, R.menu.context_item_date);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_edi = getExtra(DatesActivity.EXTRA_EDI);
		setTitle(getResources().getString(R.string.title_activity_dates_loading, m_edi));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Date item) {
		menu.setHeaderTitle(item.getDate());
	}

	@Override
	public List<Date> loadList() throws ApplicationException {
		if (m_edi != null) {
			return App.getInstance().getCineworldAccessor().getDatesForFilm(m_edi);
		} else {
			return App.getInstance().getCineworldAccessor().getAllDates();
		}
	}

	@Override
	protected ListAdapter createAdapter(final List<Date> result) {
		setTitle(getResources().getString(R.string.title_activity_dates_loaded, m_edi));
		return new DateAdapter(DatesActivity.this, result);
	}
}
