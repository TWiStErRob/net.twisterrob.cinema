package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Category;
import com.twister.cineworld.ui.adapter.CategoryAdapter;

public class CategoriesActivity extends BaseListActivity<Category> {
	public CategoriesActivity() {
		super(R.layout.activity_list, R.menu.context_item_category);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getResources().getString(R.string.title_activity_categories));
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

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Category item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_category_cinemas: {
				Intent intent = new Intent(getApplicationContext(), CinemasActivity.class);
				intent.putExtra(CinemasActivity.EXTRA_CATEGORY, item);
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_category_films: {
				Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}
}
