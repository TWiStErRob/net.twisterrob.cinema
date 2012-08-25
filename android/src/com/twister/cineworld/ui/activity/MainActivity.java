package com.twister.cineworld.ui.activity;

import java.util.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.twister.cineworld.R;
import com.twister.cineworld.ui.activity.maps.CinemasMapActivity;

public class MainActivity extends ListActivity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Map<String, Object>> listItems = getListItems();
		String[] from = { "label", "class" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		SimpleAdapter adapter = new SimpleAdapter(this, listItems, android.R.layout.simple_list_item_2, from, to);
		setListAdapter(adapter);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		@SuppressWarnings("unchecked")
		Map<String, Object> selected = (Map<String, Object>) getListView().getItemAtPosition(position);
		Intent intent = new Intent(this, (Class<?>) selected.get("class"));
		this.startActivity(intent);
	}

	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Object[] activities = {
				"Cinemas", CinemasActivity.class,
				"Cinemas map", CinemasMapActivity.class,
				"Films", FilmsActivity.class,
				"Dates", DatesActivity.class,
				"Performances", PerformancesActivity.class,
				"Categories", CategoriesActivity.class,
				"Events", EventsActivity.class,
				"Distributors", DistributorsActivity.class,
		};
		for (int i = 0; i < activities.length; i += 2) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("label", activities[i]);
			map.put("class", activities[i + 1]);
			result.add(map);
		}
		return result;
	}
}
