package com.twister.cineworld.ui.activity;

import java.net.URLEncoder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.Cinema;

public class CinemaActivity extends BaseDetailActivity<Cinema> {
	/**
	 * Cinema<br>
	 * <b>Type</b>: {@link Cinema}
	 */
	public static final String	EXTRA_CINEMA	= "cinema";
	private Cinema				m_cinema;

	public CinemaActivity() {
		super(R.layout.activity_cinema);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_cinema = (Cinema) getIntent().getExtras().get(CinemaActivity.EXTRA_CINEMA);
		setTitle(getResources().getString(R.string.title_activity_cinema_loading, m_cinema));
		startLoad();
	}

	@Override
	protected Cinema load() throws ApplicationException {
		// placeholder for further loading (e.g. how many films active and stuff)
		return m_cinema;
	}

	@Override
	protected void update(final Cinema result) {
		setTitle(getResources().getString(R.string.title_activity_cinema_loaded, result.getName()));
		TextView name = (TextView) findViewById(R.id.cinema_name);
		TextView address = (TextView) findViewById(R.id.cinema_address);
		TextView url = (TextView) findViewById(R.id.cinema_url);
		TextView phone = (TextView) findViewById(R.id.cinema_phone);

		name.setText(String.format("%s (%d)", result.getName(), result.getId()));

		// TODO find a nicer way of handling address links
		address.setText(String.format("%s, %s", result.getAddress(), result.getPostcode()));
		address.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				String query = String.format("%2$s (%1$s)", result.getAddress(), result.getPostcode());
				query = URLEncoder.encode(query);
				String uri = String.format("geo:0,0?q=%s", query);
				startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
			}
		});
		url.setText(result.getDetailsUrl());
		phone.setText(result.getTelephone());
	}
}
