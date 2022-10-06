package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.generic.Distributor;

public class DistributorAdapter extends BaseListAdapter<Distributor, DistributorAdapter.ViewHolder> {
	public DistributorAdapter(final Context context, final Collection<Distributor> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	title;
		TextView	description;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_distributor;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(android.R.id.text1);
		holder.description = (TextView) convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final Distributor currentItem, final View convertView) {
		String title = currentItem.getName();
		String description = String.format("%s\n - last updated: %2$tF %2$tT",
				currentItem.getId(), currentItem.getLastUpdate());

		holder.title.setText(title);
		holder.description.setText(description);
	}
}
