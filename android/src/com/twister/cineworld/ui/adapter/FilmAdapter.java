package com.twister.cineworld.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.generic.*;

public class FilmAdapter extends BaseListAdapter<MovieBase, FilmAdapter.ViewHolder> {
	public FilmAdapter(final Context context, final Collection<MovieBase> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	title;
		TextView	description;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_film;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(android.R.id.text1);
		holder.description = (TextView) convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final MovieBase currentItem, final View convertView) {
		String title = currentItem.getTitle();
		String description = String.format("%s (%s%s%s%s )",
				currentItem.getClassification(),
				currentItem.has2D()? " 2D" : "",
				currentItem.has3D()? " 3D" : "",
				currentItem.hasIMax2D()? " IMAX2D" : "",
				currentItem.hasIMax3D()? " IMAX3D" : "");
		if (currentItem instanceof MovieSerie) {
			title = String.format("%s (%d)", title, ((MovieSerie) currentItem).getFilms().size());
		}

		holder.title.setText(title);
		holder.description.setText(description);
	}
}
