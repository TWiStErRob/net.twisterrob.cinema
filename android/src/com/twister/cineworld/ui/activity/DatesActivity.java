package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldDate;
import com.twister.cineworld.ui.adapter.DateAdapter;

public class DatesActivity extends BaseListActivity<CineworldDate> {
	public static final String	EXTRA_EDI	= "film_edi";
	private Integer				m_edi;

	public DatesActivity() {
		super(R.layout.activity_dates, R.menu.context_item_date);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_edi = getExtra(DatesActivity.EXTRA_EDI);
		setTitle(getResources().getString(R.string.title_activity_dates_loading, m_edi));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldDate item) {
		menu.setHeaderTitle(item.getDate());
	}

	@Override
	public List<CineworldDate> loadList() throws CineworldException {
		if (m_edi != null) {
			return new CineworldAccessor().getDates(m_edi);
		} else {
			return new CineworldAccessor().getAllDates();
		}
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldDate> result) {
		setTitle(getResources().getString(R.string.title_activity_dates_loaded, m_edi));
		return new DateAdapter(DatesActivity.this, result);
	}
}
