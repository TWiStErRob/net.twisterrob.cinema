package com.twister.cineworld.ui.activity;

import java.util.List;

import android.view.ContextMenu;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Category;
import com.twister.cineworld.ui.adapter.CategoryAdapter;

public class CategoriesActivity extends BaseListActivity<Category> {
	public CategoriesActivity() {
		super(R.layout.activity_list, R.menu.context_item_category);
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Category item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected ListAdapter createAdapter(final List<Category> result) {
		return new CategoryAdapter(CategoriesActivity.this, result);
	}

	@Override
	protected List<Category> loadList() throws ApplicationException {
		return App.getInstance().getCineworldAccessor().getAllCategories();
	}

}
