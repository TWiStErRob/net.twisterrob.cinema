package com.twister.cineworld.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.ui.*;

public abstract class BaseDetailActivity<T> extends Activity implements ProgressReporter {
	private final int	m_activityLayout;

	public BaseDetailActivity(final int activityCinema) {
		m_activityLayout = activityCinema;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(m_activityLayout);
		App.getInstance().setActiveStatusBar(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.getInstance().setActiveStatusBar(this);
	}

	protected final void startLoad() {
		CineworldExecutor.execute(new CineworldGUITask<T>(this) {
			@Override
			protected T work() throws ApplicationException {
				return load();
			}

			@Override
			protected void present(final T result) {
				update(result);
			}

			@Override
			protected void exception(final ApplicationException e) {
				exceptionInternal(e);
			}

			@Override
			protected String whatAmIDoing() {
				return "loading singular data in background";
			}
		});
	}

	protected final void exceptionInternal(final ApplicationException e) {
		Toast toast = Toast.makeText(this, Translator.translate(this, e), Toast.LENGTH_SHORT);
		toast.show();
	}

	protected abstract T load() throws ApplicationException;

	protected abstract void update(final T result);

	public void reportStatus(final String message) {
		// TODO make this more sophisticated
		this.runOnUiThread(new Runnable() {
			public void run() {
				final TextView view = (TextView) findViewById(R.id.log_last_message);
				if (view != null) {
					view.setText(message);
				}
			}
		});
	}
}
