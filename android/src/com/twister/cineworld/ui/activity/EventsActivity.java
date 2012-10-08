package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getResources().getString(R.string.title_activity_events));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Event item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	public List<Event> loadList() throws ApplicationException {
		return App.getInstance().getCineworldAccessor().getAllEvents();
	}

	@Override
	protected ListAdapter createAdapter(final List<Event> result) {
		return new EventAdapter(EventsActivity.this, result);
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Event item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_event_cinemas: {
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(CinemasActivity.EXTRA_EVENT, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_event_films: {
				Intent intent = new Intent(getApplicationContext(), FilmsActivity.class);
				intent.putExtra(FilmsActivity.EXTRA_EVENT_CODE, item.getCode());
				this.startActivity(intent);
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}
}
