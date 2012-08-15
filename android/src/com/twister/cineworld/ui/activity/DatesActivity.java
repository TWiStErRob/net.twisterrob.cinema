package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldDate;
import com.twister.cineworld.ui.Tools;
import com.twister.cineworld.ui.adapter.DateAdapter;

public class DatesActivity extends BaseListActivity<CineworldDate> {
	public static final String	EXTRA_EDI	= "film_edi";
	private Integer	           m_edi;

	public DatesActivity() {
		super(R.layout.activity_dates, R.menu.context_item_date);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_edi = getExtra(DatesActivity.EXTRA_EDI);
		if (m_edi != null) {
			Tools.toast("TODO implement film filter for dates, edi=" + m_edi);
		}
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldDate item) {
		menu.setHeaderTitle(item.getDate());
	}

	@Override
	public List<CineworldDate> doWork() {
		if (m_edi != null) {
			return new CineworldAccessor().getAllDates(); // TODO filter on film
		} else {
			return new CineworldAccessor().getAllDates();
		}
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldDate> result) {
		return new DateAdapter(DatesActivity.this, result);
	}
}
