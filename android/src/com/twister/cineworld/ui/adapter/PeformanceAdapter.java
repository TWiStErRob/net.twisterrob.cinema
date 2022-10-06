package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.generic.Performance;

public class PeformanceAdapter extends BaseListAdapter<Performance, PeformanceAdapter.ViewHolder> {
	public PeformanceAdapter(final Context context, final Collection<Performance> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	title;
		TextView	description;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_performance;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(android.R.id.text1);
		holder.description = (TextView) convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final Performance currentItem, final View convertView) {
		String title = currentItem.getTime();
		String description = String.format("%s%s%s%s\n%s",
				currentItem.getType(),
				currentItem.isAvailable()? " avail" : "",
				currentItem.isSubtitled()? " sub" : "",
				currentItem.isAudioDescribed()? " ad" : "",
				currentItem.getBookingUrl()
				);

		holder.title.setText(title);
		holder.description.setText(description);
	}
}
