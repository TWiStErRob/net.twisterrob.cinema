package com.twister.cineworld.ui.activity;

import java.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.log.*;
import com.twister.cineworld.ui.adapter.*;
import com.twister.cineworld.ui.model.MainMenuItem;

public class MainActivity extends BaseListActivity<MainMenuItem> {
	private static final Log	LOG	= LogFactory.getLog(Tag.UI);

	public MainActivity() {
		super(R.layout.activity_list);
		App.getInstance().getDataBaseHelper().openDB(); // Init DB
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.info("Main activity created");
	}

	@Override
	protected ListAdapter createAdapter(final List<MainMenuItem> result) {
		return new MainMenuItemAdapter(this, result);
	}

	@Override
	protected void updateChild(final List<MainMenuItem> result) {
		super.updateChild(result);
		getSlider().getList().setAdapter(new MainMenuItemSlideMenuAdapter(this, result));
	}

	@Override
	protected List<MainMenuItem> loadList() {
		int icon = R.drawable.cineworld_logo;
		return Arrays.asList(
				new MainMenuItem("Cinemas", icon, new Intent(this, CinemasActivity.class)),
				new MainMenuItem("Cinemas map", icon, new Intent(this, CinemasMapActivity.class)),
				new MainMenuItem("Films", icon, new Intent(this, FilmsActivity.class)),
				new MainMenuItem("Dates", icon, new Intent(this, DatesActivity.class)),
				new MainMenuItem("Performances", icon, new Intent(this, PerformancesActivity.class)),
				new MainMenuItem("Categories", icon, new Intent(this, CategoriesActivity.class)),
				new MainMenuItem("Events", icon, new Intent(this, EventsActivity.class)),
				new MainMenuItem("Distributors", icon, new Intent(this, DistributorsActivity.class))
				);
	}

	@Override
	protected void onItemClick(final MainMenuItem item) {
		this.startActivity(item.getIntent());
	}
}
