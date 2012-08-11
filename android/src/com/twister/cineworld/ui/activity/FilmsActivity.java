package com.twister.cineworld.ui.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;

import com.twister.cineworld.R;
import com.twister.cineworld.model.*;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.cineworld.ui.*;
import com.twister.cineworld.ui.adapter.FilmAdapter;

public class FilmsActivity extends Activity {
	private AbsListView	m_listView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Tools.s_context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_films);

		m_listView = (AbsListView) findViewById(android.R.id.list);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			@Override
			public void run() {
				Tools.toast("Requesting");
				List<CineworldFilm> cineFilms = new CineworldAccessor().getAllFilms();
				FilmMatcher matcher = new FilmMatcher();
				List<Film> films = matcher.match(cineFilms);
				final List<FilmBase> filmList = matcher.matchSeries(films);
				FilmsActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						m_listView.setAdapter(new FilmAdapter(FilmsActivity.this, filmList));
					}
				});
				Tools.toast("Got " + films.size() + " / " + cineFilms.size());
			}
		}.start();
	}
}
