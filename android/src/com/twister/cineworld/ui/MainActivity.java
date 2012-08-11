package com.twister.cineworld.ui;

import java.util.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.twister.cineworld.R;

public class MainActivity extends ListActivity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Map<String, String>> listItems = getListItems();
		String[] from = { "label", "name" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		SimpleAdapter adapter = new SimpleAdapter(this, listItems, android.R.layout.simple_list_item_2, from, to);
		setListAdapter(adapter);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		@SuppressWarnings("unchecked")
		Map<String, String> selected = (Map<String, String>) getListView().getItemAtPosition(position);
		try {
			Intent intent = new Intent(this, Class.forName(selected.get("name")));
			this.startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<Map<String, String>> getListItems() {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String[] activities = {
				FilmsActivity.class.getName(), "Films",
				CinemasActivity.class.getName(), "Cinemas",
				CategoriesActivity.class.getName(), "Categories",
				EventsActivity.class.getName(), "Events",
				DistributorsActivity.class.getName(), "Distributors",
		};
		for (int i = 0; i < activities.length; i += 2) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", activities[i]);
			map.put("label", activities[i + 1]);
			result.add(map);
		}
		return result;
	}
}
