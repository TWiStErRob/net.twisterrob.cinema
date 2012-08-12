package com.twister.cineworld.ui.activity;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
		TextView name = (TextView) findViewById(R.id.cinema_name);
		TextView address = (TextView) findViewById(R.id.cinema_address);
		TextView url = (TextView) findViewById(R.id.cinema_url);
		TextView phone = (TextView) findViewById(R.id.cinema_phone);

		name.setText(String.format("%s (%d)", result.getName(), result.getId()));

		// TODO find a nicer way of handlink address links
		address.setText(String.format("%s, %s", result.getAddress(), result.getPostcode()));
		address.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				String query = String.format("%2$s (%1$s)", result.getAddress(), result.getPostcode());
				query = URLEncoder.encode(query);
				String uri = String.format("geo:0,0?q=%s", query);
				startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
			}
		});
		url.setText(result.getCinemaUrl());
		phone.setText(result.getTelephone());
	}
}
