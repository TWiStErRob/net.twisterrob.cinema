package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldCategory;
import com.twister.cineworld.ui.adapter.CategoryAdapter;

public class CategoriesActivity extends BaseListActivity<CineworldCategory> {
	public CategoriesActivity() {
		super(R.layout.activity_categories, R.menu.context_item_category);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final CineworldCategory item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected ListAdapter createAdapter(final List<CineworldCategory> result) {
		return new CategoryAdapter(CategoriesActivity.this, result);
	}

	@Override
	protected List<CineworldCategory> doWork() {
		return new CineworldAccessor().getAllCategories();
	}

}
