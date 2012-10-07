package com.twister.cineworld.ui.activity;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.model.generic.*;
import com.twister.cineworld.ui.adapter.CinemaAdapter;

public class CinemasActivity extends BaseListActivity<Cinema> {
	/**
	 * Film<br>
	 * <b>Type</b>: {@link Film}
	 */
	public static final String	EXTRA_FILM			= "film";
	/**
	 * Distributor<br>
	 * <b>Type</b>: {@link Distributor}
	 */
	public static final String	EXTRA_DISTRIBUTOR	= "distributor";
	private UIRequest			m_request;

	public CinemasActivity() {
		super(R.layout.activity_cinemas, R.menu.context_item_cinema, false);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_request = new UIRequest(this.getIntent());
		startLoad();
		setTitle(m_request.getTitle(getResources()));
	}

	@Override
	protected void onCreateContextMenu(final ContextMenu menu, final Cinema item) {
		menu.setHeaderTitle(item.getName());
	}

	@Override
	protected boolean onContextItemSelected(final MenuItem menu, final Cinema item) {
		switch (menu.getItemId()) {
			case R.id.menuitem_cinema_details: {
				Intent intent = new Intent(getApplicationContext(), CinemaActivity.class);
				intent.putExtra(CinemaActivity.EXTRA_ID, item.getId());
				this.startActivity(intent);
				return true;
			}
			case R.id.menuitem_cinema_films: {
				Intent intent = new Intent(getApplicationContext(), FilmsActivity.class);
				intent.putExtra(FilmsActivity.EXTRA_CINEMA_ID, item.getId());
				this.startActivity(intent);
				return true;
			}
			default:
				return super.onContextItemSelected(menu, item);
		}
	}

	@Override
	protected List<Cinema> loadList() throws ApplicationException {
		return m_request.getList();
	}

	@Override
	protected ListAdapter createAdapter(final List<Cinema> result) {
		setTitle(m_request.getTitle(getResources()));
		return new CinemaAdapter(this, result);
	}

	private static final class UIRequest {
		private final Intent		m_intent;
		private final Film			m_film;
		private final Distributor	m_distributor;

		public UIRequest(final Intent intent) {
			m_intent = intent;
			m_film = getExtra(CinemasActivity.EXTRA_FILM);
			m_distributor = getExtra(CinemasActivity.EXTRA_DISTRIBUTOR);
		}

		public String getTitle(final Resources resources) {
			if (m_film != null) {
				return resources.getString(R.string.title_activity_cinemas_forFilm, m_film.getTitle());
			} else if (m_distributor != null) {
				return resources.getString(R.string.title_activity_cinemas_forDistributor, m_distributor.getName());
			} else {
				return resources.getString(R.string.title_activity_cinemas_all, m_film);
			}
		}

		public List<Cinema> getList() throws ApplicationException {
			if (m_film != null) {
				return App.getInstance().getCineworldAccessor().getCinemasForFilm(m_film.getEdi());
			} else if (m_distributor != null) {
				return App.getInstance().getCineworldAccessor().getCinemasForDistributor(m_distributor.getId());
			} else {
				return App.getInstance().getCineworldAccessor().getAllCinemas();
			}
		}

		@SuppressWarnings("unchecked")
		public <T> T getExtra(final String extraKey) {
			T result = null;
			if (m_intent.hasExtra(extraKey)) {
				Object object = m_intent.getExtras().get(extraKey);
				result = (T) object;
			}
			return result;
		}
	}
}
