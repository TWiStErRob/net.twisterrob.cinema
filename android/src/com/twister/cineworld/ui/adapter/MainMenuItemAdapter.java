package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.ui.model.MainMenuItem;

public class MainMenuItemAdapter extends BaseListAdapter<MainMenuItem, MainMenuItemAdapter.ViewHolder> {
	public MainMenuItemAdapter(final Context context, final Collection<MainMenuItem> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	title;
		TextView	clazz;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_main;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.listitem_main_title);
		holder.clazz = (TextView) convertView.findViewById(R.id.listitem_main_class);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final MainMenuItem currentItem, final View convertView) {
		int icon = currentItem.getIcon();
		String label = currentItem.getTitle();
		String clazz = currentItem.getIntent().getComponent().getClassName();

		holder.title.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
		holder.title.setText(label);
		holder.clazz.setText(clazz);
	}
}
