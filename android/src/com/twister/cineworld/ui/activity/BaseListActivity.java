package com.twister.cineworld.ui.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.ui.*;

/**
 * Base class for listing related activities handling common UI stuff generic to all of them.<br>
 * <code>RawItem</code> and <code>UIItem</code> may be the same.
 * 
 * @author papp.robert.s
 * @param <RawItem> The type of items returned by the lower data handling layers
 * @param <UIItem> The type of items handled on the UI
 * @see ListRetriever
 */
public abstract class BaseListActivity<RawItem, UIItem> extends Activity {
	private AbsListView	m_listView;
	private int			m_contentViewId;
	private int			m_contextMenuId;

	/**
	 * Creates an instace of the base class. <code>contentViewId</code> will be set with {@link #setContentView(int)}
	 * and <code>contextMenuId</code> will be inflated in
	 * {@link #onCreateContextMenu(ContextMenu, View, ContextMenuInfo)}
	 * 
	 * @param contentViewId Must have an {@link AbsListView} with an id of <code>android:id="@android:id/list"</code>.
	 * @param contextMenuId Context menu for items in the list.
	 */
	public BaseListActivity(final int contentViewId, final int contextMenuId) {
		m_contentViewId = contentViewId;
		m_contextMenuId = contextMenuId;
	}

	/**
	 * Prepare the activity's UI and the list.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.s_context = this;
		setContentView(m_contentViewId);

		m_listView = (AbsListView) findViewById(android.R.id.list);
		registerForContextMenu(m_listView);
	}

	/**
	 * Executes the implementation of {@link ListRetriever} in this activity asynchronously.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see AsyncRetrieverExecutor
	 */
	@Override
	protected void onStart() {
		super.onStart();
		CinewordExecutor.execute(new CinewordGUITask<List<UIItem>>(this) {
			@Override
			protected List<UIItem> work() throws CineworldException {
				return doWork();
			}

			@Override
			protected void present(final List<UIItem> result) {
				update(result);
			}

			@Override
			protected void exception(final CineworldException e) {
				doException(e);
			}

		});
	}

	protected void doException(final CineworldException e) {
		Log.e("Cineworld", "Error in " + getClass().getSimpleName(), e);
		// TODO show the user
	}

	protected abstract List<UIItem> doWork();

	/**
	 * Creates the context menu based on the <code>contextMenuId</code> given in the constructor. Extenders must use
	 * {@link #onCreateContextMenu(ContextMenu, Object)} to customize the menu based on the selected item.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see #onCreateContextMenu(ContextMenu, Object)
	 */
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

	/**
	 * Extenders must do any customization of the context menu in this method.
	 * 
	 * @param menu The context menu to be customized.
	 * @param item The selected item from the adapter.
	 * @see #onCreateContextMenu(ContextMenu, View, ContextMenuInfo)
	 */
	protected abstract void onCreateContextMenu(final ContextMenu menu, final UIItem item);

	/**
	 * Delegates logic to {@link #onContextItemSelected(MenuItem, Object)}, where the selected item is known.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public final boolean onContextItemSelected(final MenuItem menu) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menu.getMenuInfo();
		@SuppressWarnings("unchecked")
		UIItem adapterItem = (UIItem) m_listView.getAdapter().getItem((int) info.id);
		return onContextItemSelected(menu, adapterItem);
	}

	/**
	 * Derived classes should call through to the base class for it to perform the default menu handling.
	 * 
	 * @param menu The context menu item that was selected.
	 * @param item The list adapter item that was selected.
	 * @return Return false to allow normal context menu processing to proceed, true to consume it here.
	 * @see #onContextItemSelected(MenuItem)
	 */
	protected boolean onContextItemSelected(final MenuItem menu, final UIItem item) {
		return super.onContextItemSelected(menu);
	}

	/**
	 * Updates the list with the new adapter.
	 * 
	 * @see #createAdapter(List)
	 */
	public final void update(final List<UIItem> result) {
		m_listView.setAdapter(createAdapter(result));
	}

	/**
	 * Creates an adapter for the list in the current activity.
	 * 
	 * @param result the items to be displayed in the list
	 * @return the adapter to be used for the list
	 * @see #update(List)
	 */
	protected abstract ListAdapter createAdapter(List<UIItem> result);

	/**
	 * Should not be used by children, if necessary re-think/generalize your design.
	 * 
	 * @author papp.robert.s
	 */
	@SuppressWarnings("unused")
	private AbsListView getListView() {
		return m_listView;
	}
}
