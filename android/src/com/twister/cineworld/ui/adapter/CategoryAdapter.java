package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.generic.Category;

public class CategoryAdapter extends BaseListAdapter<Category, CategoryAdapter.ViewHolder> {
	public CategoryAdapter(final Context context, final Collection<Category> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	title;
		TextView	description;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_category;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(android.R.id.text1);
		holder.description = (TextView) convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final Category currentItem, final View convertView) {
		String title = currentItem.getName();
		String description = String.format("%s", currentItem.getCode());

		holder.title.setText(title);
		holder.description.setText(description);
	}
}
