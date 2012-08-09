package com.twister.cineworld.ui;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.*;

public class FilmAdapter extends BaseListAdapter<FilmBase, FilmAdapter.ViewHolder> {
	public FilmAdapter(final Context context, final Collection<FilmBase> items) {
		super(context, items);
	}

	protected class ViewHolder {
		TextView	character;
		TextView	morse;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_films;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.character = (TextView) convertView.findViewById(android.R.id.text1);
		holder.morse = (TextView) convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final FilmBase currentItem, final View convertView) {
		String characterText = currentItem.getTitle();
		String morseText = String.format("%s (%s%s%s%s )",
				currentItem.getClassification(),
				currentItem.has2D()? " 2D" : "",
				currentItem.has3D()? " 3D" : "",
				currentItem.hasIMax2D()? " IMAX2D" : "",
				currentItem.hasIMax3D()? " IMAX3D" : "");
		if (currentItem instanceof FilmSerie) {
			characterText = String.format("%s (%d)", characterText, ((FilmSerie) currentItem).getFilms().size());
		}

		holder.character.setText(characterText);
		holder.morse.setText(morseText);
	}
}
