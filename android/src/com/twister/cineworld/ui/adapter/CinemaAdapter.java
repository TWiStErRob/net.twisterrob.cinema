package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.data.CineworldCinema;

public class CinemaAdapter extends BaseListAdapter<CineworldCinema, CinemaAdapter.ViewHolder> {
	public CinemaAdapter(final Context context, final Collection<CineworldCinema> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	title;
		TextView	description;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_cinema;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(android.R.id.text1);
		holder.description = (TextView) convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final CineworldCinema currentItem, final View convertView) {
		String title = currentItem.getName();
		String description = String.format("%s, %s",
				currentItem.getAddress(),
				currentItem.getPostcode()
				);

		holder.title.setText(title);
		holder.description.setText(description);
	}
}
