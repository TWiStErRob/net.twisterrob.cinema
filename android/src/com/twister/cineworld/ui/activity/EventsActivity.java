package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Event;
import com.twister.cineworld.ui.adapter.EventAdapter;

public class EventsActivity extends BaseListActivity<Event> {
	public EventsActivity() {
		super(R.layout.activity_list, R.menu.context_item_event);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Event item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	public List<Event> loadList() throws ApplicationException {
		return App.getInstance().getCineworldAccessor().getAllEvents();
	}

	public List<Event> process(final List<Event> list) {
		return list;
	}

	@Override
	protected ListAdapter createAdapter(final List<Event> result) {
		return new EventAdapter(EventsActivity.this, result);
	}
}
