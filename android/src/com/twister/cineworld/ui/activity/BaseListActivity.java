package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.twister.cineworld.*;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.ui.*;
import com.twister.cineworld.ui.components.*;

/**
 * Base class for listing related activities handling common UI stuff generic to all of them.
 * 
 * @author papp.robert.s
 * @param <UIItem> The type of items handled on the UI
 */
public abstract class BaseListActivity<UIItem> extends VerboseActivity implements OnItemClickListener, ProgressReporter {
	private static final int	VIEW_LOADING_ID	= 0;
	private static final int	VIEW_LIST_ID	= 1;
	private static final int	VIEW_EMPTY_ID	= 2;
	private AbsListView			m_listView;
	private ViewAnimator		m_views;
	private int					m_contentViewId;
	/**
	 * Context menu for items in the list. <code>null</code>, if there is no context menu
	 */
	private Integer				m_contextMenuId;
	private Integer				m_optionsMenuId;
	private SlideMenu			m_slidemenu;
	private boolean				m_autoLoad		= true;

	/**
	 * Creates an instance of the base class. <code>contentViewId</code> will be set with {@link #setContentView(int)}
	 * and <code>contextMenuId</code> will be inflated in
	 * {@link #onCreateContextMenu(ContextMenu, View, ContextMenuInfo)}
	 * 
	 * @param contentViewId Must have an {@link AbsListView} with an id of <code>android:id="@android:id/list"</code>.
	 * @param contextMenuId Context menu for items in the list.
	 */
	public BaseListActivity(final int contentViewId) {
		m_contentViewId = contentViewId;
	}

	/**
	 * Prepare the activity's UI and the list.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(m_contentViewId);
		App.getInstance().setActiveStatusBar(this);

		m_listView = (AbsListView) findViewById(android.R.id.list);
		m_views = (ViewAnimator) findViewById(android.R.id.toggle);
		((ImageView) findViewById(R.id.image_loading)) // TODO do we need to stop the animation?
				.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_loading));
		registerForContextMenu(m_listView);
		m_listView.setOnItemClickListener(this);

		m_slidemenu = new SlideMenu(this);
		m_slidemenu.checkEnabled();
		m_slidemenu.getList().setOnItemClickListener(new SliderMenuMainMenuListener(m_slidemenu));

		if (m_autoLoad) {
			startLoad();
		}
	}

	public void setContextMenu(final int contextMenuId) {
		m_contextMenuId = contextMenuId;
	}

	public Integer getContextMenu() {
		return m_contextMenuId;
	}

	public void clearContextMenu() {
		m_contextMenuId = null;
	}

	public void setOptionsMenu(final int optionsMenuId) {
		m_optionsMenuId = optionsMenuId;
	}

	public Integer getOptionsMenu() {
		return m_optionsMenuId;
	}

	public void clearOptionsMenu() {
		m_optionsMenuId = null;
	}

	public void setAutoLoad(final boolean autoLoad) {
		m_autoLoad = autoLoad;
	}

	public boolean isAutoLoad() {
		return m_autoLoad;
	}

	/**
	 * Initiates data loading.
	 */
	protected final void startLoad() {
		m_views.setDisplayedChild(VIEW_LOADING_ID);
		// start data loading
		CineworldExecutor.execute(new CineworldGUITask<List<UIItem>>(this) {
			@Override
			protected List<UIItem> work() throws ApplicationException {
				return BaseListActivity.this.loadList();
			}

			@Override
			protected void present(final List<UIItem> result) {
				BaseListActivity.this.displayList(result);
			}

			@Override
			protected void exception(final ApplicationException e) {
				exceptionInternal(e);
			}

			@Override
			protected String whatAmIDoing() {
				return "loading list data in background";
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.getInstance().setActiveStatusBar(this);
	}

	/**
	 * Override this method in case something has to be done in case an exception happens other than user notification
	 * and logging.
	 * 
	 * @param exception the exception
	 */
	protected void exception(final ApplicationException exception) {
		// optionally overridden by children
	}

	/**
	 * Data acquisition is called in this method. This will be executed on a background thread.
	 * 
	 * @return a {@link List} of items to be presented
	 * @throws ApplicationException if data could not be loaded
	 */
	protected abstract List<UIItem> loadList() throws ApplicationException;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		if (m_optionsMenuId != null) {
			getMenuInflater().inflate(m_optionsMenuId, menu);
		}
		return true;
	}

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
		if (m_contextMenuId != null) {
			getMenuInflater().inflate(m_contextMenuId, menu);

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

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Toast.makeText(this, String.format("Not implemented: %s", item.getTitle()), Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
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
		Toast.makeText(this, String.format("Not implemented: %s", menu.getTitle()), Toast.LENGTH_SHORT).show();
		return super.onContextItemSelected(menu);
	}

	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		@SuppressWarnings("unchecked")
		UIItem item = (UIItem) parent.getItemAtPosition(position);
		onItemClick(item);
	}

	protected void onItemClick(final UIItem item) {
	}

	/**
	 * R.layout.common_header/R.id.button_home handler
	 */
	public void buttonHome_Click(final View v) {
		m_slidemenu.show(true);
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
		if (result.isEmpty()) {
			BaseListActivity.this.m_views.setDisplayedChild(VIEW_EMPTY_ID);
		} else {
			BaseListActivity.this.m_views.setDisplayedChild(VIEW_LIST_ID);
			m_listView.setAdapter(createAdapter(result));
			updateChild(result);
		}
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

	/**
	 * @deprecated use UIRequest like CinemasActivity
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	protected <T> T getExtra(final String extraKey) {
		T result = null;
		if (getIntent().hasExtra(extraKey)) {
			Object object = getIntent().getExtras().get(extraKey);
			result = (T) object;
		}
		return result;
	}

	private final void exceptionInternal(final ApplicationException e) {
		Toast toast = Toast.makeText(this, Translator.translate(this, e), Toast.LENGTH_SHORT);
		toast.show();
		BaseListActivity.this.exception(e);
	}

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
