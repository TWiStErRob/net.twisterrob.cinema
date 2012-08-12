package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldEvent;
import com.twister.cineworld.ui.adapter.EventAdapter;

public class EventsActivity extends BaseListActivity<CineworldEvent, CineworldEvent> {
	public EventsActivity() {
		super(R.layout.activity_events, R.menu.context_item_event);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldEvent item) {
		menu.setHeaderTitle(item.getName());
	}

	public List<CineworldEvent> retrieve() {
		return new CineworldAccessor().getAllEvents();
	}

	public List<CineworldEvent> process(final List<CineworldEvent> list) {
		return list;
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldEvent> result) {
		return new EventAdapter(EventsActivity.this, result);
	}
}
