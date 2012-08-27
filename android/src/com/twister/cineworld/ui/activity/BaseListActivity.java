package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.twister.cineworld.R;
import com.twister.cineworld.exception.CineworldException;
import com.twister.cineworld.ui.*;
import com.twister.cineworld.ui.components.*;

/**
 * Base class for listing related activities handling common UI stuff generic to all of them.
 * 
 * @author papp.robert.s
 * @param <UIItem> The type of items handled on the UI
 */
public abstract class BaseListActivity<UIItem> extends VerboseActivity implements OnItemClickListener {
	private AbsListView	m_listView;
	private int			m_contentViewId;
	/**
	 * Context menu for items in the list. <code>null</code>, if there is no context menu
	 */
	private Integer		m_contextMenuId;
	private SlideMenu	m_slidemenu;

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

	public BaseListActivity(final int contentViewId) {
		m_contentViewId = contentViewId;
		m_contextMenuId = null;
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(m_contentViewId);

		m_listView = (AbsListView) findViewById(android.R.id.list);
		registerForContextMenu(m_listView);
		m_listView.setOnItemClickListener(this);

		m_slidemenu = new SlideMenu(this);
		m_slidemenu.checkEnabled();
		m_slidemenu.getList().setOnItemClickListener(new SliderMenuMainMenuListener(m_slidemenu));

		startLoad();
	}

	/**
	 * Initiates data loading.
	 */
	protected void startLoad() {
		// start data loading
		CineworldExecutor.execute(new CineworldGUITask<List<UIItem>>(this) {
			@Override
			protected List<UIItem> work() throws CineworldException {
				return BaseListActivity.this.loadList();
			}

			@Override
			protected void present(final List<UIItem> result) {
				BaseListActivity.this.displayList(result);
			}

			@Override
			protected void exception(final CineworldException e) {
				BaseListActivity.this.exception(e);
			}
		});
	}

	/**
	 * Override this method in case something has to be done in case an exception happens other than user notification
	 * and logging.
	 * 
	 * @param exception the exception
	 */
	protected void exception(final CineworldException exception) {
		// optionally overridden by children
	}

	/**
	 * Data acquisition is called in this method. This will be executed on a background thread.
	 * 
	 * @return a {@link List} of items to be presented
	 */
	protected abstract List<UIItem> loadList();

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
		if (m_contextMenuId != null) {
			inflater.inflate(m_contextMenuId, menu);

			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			assert v == m_listView;
			AbsListView list = (AbsListView) v;
			@SuppressWarnings("unchecked")
			UIItem adapterItem = (UIItem) list.getAdapter().getItem((int) info.id);
			onCreateContextMenu(menu, adapterItem);
		}
	}

	@Override
	public void setTitle(final int titleId) {
		super.setTitle(titleId);
		TextView header = getHeaderTitle();
		if (header != null) {
			header.setText(titleId);
		}
	}

	@Override
	public void setTitle(final CharSequence title) {
		super.setTitle(title);
		TextView header = getHeaderTitle();
		if (header != null) {
			header.setText(title);
		}
	}

	private TextView getHeaderTitle() {
		return (TextView) this.findViewById(R.id.activity_title);
	}

	/**
	 * Extenders must do any customization of the context menu in this method.
	 * 
	 * @param menu The context menu to be customized.
	 * @param item The selected item from the adapter.
	 * @see #onCreateContextMenu(ContextMenu, View, ContextMenuInfo)
	 */
	protected void onCreateContextMenu(final ContextMenu menu, final UIItem item) {
	}

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

	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		@SuppressWarnings("unchecked")
		UIItem item = (UIItem) parent.getItemAtPosition(position);
		onItemClick(item);
	}

	protected void onItemClick(final UIItem item) {
	}

	public SlideMenu getSlider() {
		return m_slidemenu;
	}

	/**
	 * Updates the list with the new adapter.
	 * 
	 * @see #createAdapter(List)
	 */
	protected final void displayList(final List<UIItem> result) {
		m_listView.setAdapter(createAdapter(result));
		updateChild(result);
	}

	protected void updateChild(final List<UIItem> result) {
	}

	/**
	 * Creates an adapter for the list in the current activity.
	 * 
	 * @param result the items to be displayed in the list
	 * @return the adapter to be used for the list
	 * @see #displayList(List)
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

	@SuppressWarnings("unchecked")
	protected <T> T getExtra(final String extraKey) {
		T result = null;
		if (getIntent().hasExtra(extraKey)) {
			Object object = getIntent().getExtras().get(extraKey);
			result = (T) object;
		}
		return result;
	}
}
