package com.twister.cineworld.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldFilm;
import com.twister.cineworld.ui.*;

public class FilmActivity extends Activity {
	/**
	 * Film EDI<br>
	 * <b>Type</b>: Integer
	 */
	public static final String	EXTRA_EDI	= "extra_edi";
	private Integer	           m_edi;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_film);
		m_edi = (Integer) getIntent().getExtras().get(FilmActivity.EXTRA_EDI);
		setTitle(getResources().getString(R.string.title_activity_film_loading, m_edi));

		CinewordExecutor.execute(new CinewordGUITask<CineworldFilm>(this) {
			@Override
			protected CineworldFilm work() throws CineworldException {
				return new CineworldAccessor().getFilm(m_edi);
			}

			@Override
			protected void present(final CineworldFilm result) {
				update(result);
			}

			@Override
			protected void exception(final CineworldException e) {
				Log.e("FilmActivity", "Error in FilmActivity", e);
				// TODO show the user
			}
		});
	}

	private void update(final CineworldFilm result) {
		setTitle(getResources().getString(R.string.title_activity_film_loaded, result.getTitle()));
		TextView title = (TextView) findViewById(R.id.film_title);
		TextView attributes = (TextView) findViewById(R.id.film_attributes);
		TextView classification = (TextView) findViewById(R.id.film_classification);
		TextView advisory = (TextView) findViewById(R.id.film_advisory);
		TextView url_details = (TextView) findViewById(R.id.film_url_details);
		TextView url_still = (TextView) findViewById(R.id.film_url_still);
		TextView url_poster = (TextView) findViewById(R.id.film_url_poster);

		title.setText(String.format("%s (%d/%d)", result.getTitle(), result.getEdi(), result.getId()));
		attributes.setText(String.format("%s %s", result.isIMax()? "IMAX" : "regular", result.is3D()? "3D" : "2D"));
		classification.setText(result.getClassification());
		advisory.setText(result.getAdvisory());
		url_details.setText(result.getFilmUrl());
		url_still.setText(result.getStillUrl());
		url_poster.setText(result.getPosterUrl());
	}
}
