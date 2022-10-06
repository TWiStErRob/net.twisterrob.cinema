package com.twister.cineworld.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.android.maps.MapActivity;
import com.twister.cineworld.R;
import com.twister.cineworld.exception.ApplicationException;
import com.twister.cineworld.ui.*;
import com.twister.cineworld.ui.components.*;

/**
 * Base class for listing related activities on a map, handling common UI stuff generic to all of them.<br>
 * 
 * @author papp.robert.s
 * @param <UIItem> The type of items handled on the UI
 */
public abstract class BaseListMapActivity<UIItem> extends MapActivity {
	private AdapterView<? extends Adapter>	m_adapterView;
	private int								m_contentViewId;
	private int								m_contextMenuId;
	private boolean							m_autoLoad;
	private Integer							m_optionsMenuId;
	private SlideMenu						m_slidemenu;

	/**
	 * Creates an instance of the base class. <code>contentViewId</code> will be set with {@link #setContentView(int)}
	 * and <code>contextMenuId</code> will be inflated in
	 * {@link #onCreateContextMenu(ContextMenu, View, ContextMenuInfo)}
	 * 
	 * @param contentViewId Must have an {@link AbsListView} with an id of <code>android:id="@android:id/list"</code>.
	 * @param contextMenuId Context menu for items in the list.
	 */
	public BaseListMapActivity(final int contentViewId, final int contextMenuId) {
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(m_contentViewId);

		m_adapterView = (AdapterView<?>) findViewById(android.R.id.list);
		registerForContextMenu(m_adapterView);

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

	public int getContextMenu() {
		return m_contextMenuId;
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
		CineworldExecutor.execute(new CineworldGUITask<List<UIItem>>(this) {
			@Override
			protected List<UIItem> work() throws ApplicationException {
				return loadList();
			}

			@Override
			protected void present(final List<UIItem> result) {
				update(result);
			}

			@Override
			protected void exception(final ApplicationException exception) {
				exceptionInternal(exception);
			}

			@Override
			protected String whatAmIDoing() {
				return "load list in background";
			}
		});
	}

	private final void exceptionInternal(final ApplicationException exception) {
		Toast toast = Toast.makeText(this, Translator.translate(this, exception),
				Toast.LENGTH_SHORT);
		toast.show();
	}

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
		getMenuInflater().inflate(m_contextMenuId, menu);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		assert v == m_adapterView;
		AdapterView<? extends Adapter> list = (AdapterView<?>) v;
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
		UIItem adapterItem = (UIItem) m_adapterView.getAdapter().getItem((int) info.id);
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
	public void update(final List<UIItem> result) {
		if (m_adapterView instanceof AbsListView) {
			((AbsListView) m_adapterView).setAdapter((ListAdapter) createAdapter(result));
		} else {
			((AbsSpinner) m_adapterView).setAdapter((SpinnerAdapter) createAdapter(result));
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
	 * Creates an adapter for the list in the current activity.
	 * 
	 * @param result the items to be displayed in the list
	 * @return the adapter to be used for the list
	 * @see #update(List)
	 */
	protected abstract Adapter createAdapter(List<UIItem> result);

	/**
	 * Should not be used by children, if necessary re-think/generalize your design.
	 * 
	 * @author papp.robert.s
	 */
	protected AbsListView getListView() {
		return (AbsListView) m_adapterView;
	}

	/**
	 * Should not be used by children, if necessary re-think/generalize your design.
	 * 
	 * @author papp.robert.s
	 */
	protected AbsSpinner getSpinner() {
		return (AbsSpinner) m_adapterView;
	}

	/**
	 * Data acquisition is called in this method. This will be executed on a background thread.
	 * 
	 * @return a {@link List} of items to be presented
	 * @throws ApplicationException if data could not be loaded
	 */
	protected abstract List<UIItem> loadList() throws ApplicationException;

	/**
	 * R.layout.common_header/R.id.button_home handler
	 */
	public void buttonHome_Click(final View v) {
		m_slidemenu.show(true);
	}

	public SlideMenu getSlider() {
		return m_slidemenu;
	}
}
