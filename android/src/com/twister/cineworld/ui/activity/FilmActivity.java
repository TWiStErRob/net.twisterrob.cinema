package com.twister.cineworld.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Film;

public class FilmActivity extends BaseDetailActivity<Film> {
	/**
	 * Film EDI<br>
	 * <b>Type</b>: Integer
	 */
	public static final String	EXTRA_EDI	= "extra_edi";
	private Integer				m_edi;

	public FilmActivity() {
		super(R.layout.activity_film);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_edi = (Integer) getIntent().getExtras().get(FilmActivity.EXTRA_EDI);
		setTitle(getResources().getString(R.string.title_activity_film_loading, m_edi));
		startLoad();
	}

	@Override
	protected Film load() throws ApplicationException {
		return App.getInstance().getCineworldAccessor().getFilm(m_edi);
	}

	@Override
	protected void update(final Film result) {
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
