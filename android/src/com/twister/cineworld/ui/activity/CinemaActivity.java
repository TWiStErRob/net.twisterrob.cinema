package com.twister.cineworld.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.twister.cineworld.R;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldCinema;
import com.twister.cineworld.ui.*;

public class CinemaActivity extends Activity implements Retriever<CineworldCinema, CineworldCinema> {
	public static final String	EXTRA_ID	= "extra_id";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cinema);

		new AsyncRetrieverExecutor<CineworldCinema, CineworldCinema>(this).execute(this);
	}

	public CineworldCinema retrieve() {
		Long id = (Long) getIntent().getExtras().get(CinemaActivity.EXTRA_ID);
		return new CineworldAccessor().getCinema(id);
	}

	public CineworldCinema process(final CineworldCinema item) {
		return item;
	}

	public void update(final CineworldCinema result) {
		setTitle(String.format("Cinema details: %s", result.getName()));
	}
}
