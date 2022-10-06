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
import com.twister.cineworld.tools.StringTools;

public class CinemaActivity extends BaseDetailActivity<Cinema> {
	private CinemaUIRequest	m_request;

	public CinemaActivity() {
		super(R.layout.activity_cinema);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new CinemaUIRequest(getIntent());
		startLoad();
		setTitle(m_request.getTitle(getResources()));
	}

	@Override
	protected Cinema load() throws ApplicationException {
		// placeholder for further loading (e.g. how many films active and stuff)
		return m_request.getCinema();
	}

	@Override
	protected void update(final Cinema result) {
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
		url.setText(StringTools.toNullString(result.getDetailsUrl(), "<no details url>"));
		phone.setText(result.getTelephone());
	}
}
