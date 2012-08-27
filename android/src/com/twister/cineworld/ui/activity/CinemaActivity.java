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
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.log.*;
import com.twister.cineworld.model.json.CineworldAccessor;
import com.twister.cineworld.model.json.data.CineworldCinema;
import com.twister.cineworld.ui.*;

public class CinemaActivity extends Activity {

	private static final CineworldLogger	LOG			= LogFactory.getLog(Tag.UI);

	/**
	 * Cinema ID<br>
	 * <b>Type</b>: Integer
	 */
	public static final String				EXTRA_ID	= "extra_id";
	private Integer							m_id;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cinema);
		m_id = (Integer) getIntent().getExtras().get(CinemaActivity.EXTRA_ID);
		setTitle(getResources().getString(R.string.title_activity_cinema_loading, m_id));

		CineworldExecutor.execute(new CineworldGUITask<CineworldCinema>(this) {
			@Override
			protected CineworldCinema work() throws CineworldException {
				return new CineworldAccessor().getCinema(m_id);
			}

			@Override
			protected void present(final CineworldCinema result) {
				update(result);
			}

			@Override
			protected void exception(final CineworldException e) {
				CinemaActivity.LOG.error("Error in CinemaActivity", e);
				// TODO show the user
			}
		});
	}

	private void update(final CineworldCinema result) {
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
		url.setText(result.getCinemaUrl());
		phone.setText(result.getTelephone());
	}
}
