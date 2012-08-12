package com.twister.cineworld.ui.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.twister.cineworld.ui.*;

public abstract class BaseListActivity<RawItem, UIItem> extends Activity implements Retriever<RawItem, UIItem> {
	private AbsListView	m_listView;
	private int			m_contentViewId;
	private int			m_contextMenuId;

	public BaseListActivity(final int contentViewId, final int contextMenuId) {
		m_contentViewId = contentViewId;
		m_contextMenuId = contextMenuId;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.s_context = this;
		setContentView(m_contentViewId);

		m_listView = (AbsListView) findViewById(android.R.id.list);
		registerForContextMenu(m_listView);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new AsyncRetrieverExecutor(this).execute(this);
	}

	@Override
	public final void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(m_contextMenuId, menu);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		assert v == m_listView;
		AbsListView list = (AbsListView) v;
		@SuppressWarnings("unchecked")
		UIItem adapterItem = (UIItem) list.getAdapter().getItem((int) info.id);
		onCreateContextMenu(menu, adapterItem);
	}

	protected abstract void onCreateContextMenu(final ContextMenu menu, final UIItem item);

	@Override
	public final boolean onContextItemSelected(final MenuItem menu) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menu.getMenuInfo();
		@SuppressWarnings("unchecked")
		UIItem adapterItem = (UIItem) m_listView.getAdapter().getItem((int) info.id);
		return onContextItemSelected(menu, adapterItem);
	}

	protected boolean onContextItemSelected(final MenuItem menu, final UIItem item) {
		return super.onContextItemSelected(menu);
	}

	public final void update(final List<UIItem> result) {
		getListView().setAdapter(createAdapter(result));
	}

	protected abstract ListAdapter createAdapter(List<UIItem> result);

	public AbsListView getListView() {
		return m_listView;
	}
}
