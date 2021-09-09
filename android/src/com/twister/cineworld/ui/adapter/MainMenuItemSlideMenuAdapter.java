package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.*;

import com.twister.cineworld.R;
import com.twister.cineworld.ui.model.MainMenuItem;

public class MainMenuItemSlideMenuAdapter extends
		BaseListAdapter<MainMenuItem, MainMenuItemSlideMenuAdapter.ViewHolder> {
	public MainMenuItemSlideMenuAdapter(final Context context, final Collection<MainMenuItem> items) {
		super(context, items);
	}

	protected class ViewHolder {
		ImageView	icon;
		TextView	label;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_home_menu;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.menu_icon);
		holder.label = (TextView) convertView.findViewById(R.id.menu_label);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final MainMenuItem currentItem, final View convertView) {
		String label = currentItem.getTitle();
		int icon = currentItem.getIcon();

		holder.label.setText(label);
		holder.icon.setImageResource(icon);
	}
}
