package com.twister.cineworld.ui.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import com.twister.cineworld.R;
import com.twister.cineworld.log.*;

public class SlideMenu {
	private static final int	DURATION	= 200;
	private static final Log	LOG			= LogFactory.getLog(Tag.UI);
	private static boolean		s_menuShown	= false;
	private static View			s_menu;
	private static LinearLayout	s_content;
	private static FrameLayout	s_parent;
	private static int			s_menuSize;
	private Activity			m_activity;

	public SlideMenu(final Activity activity) {
		this.m_activity = activity;
		init();
	}

	private void init() {
		LOG.verbose("init()- menuShown: %s", s_menuShown);
		s_menuSize = (int) (m_activity.getResources().getDisplayMetrics().widthPixels * 0.80f);
		s_content = ((LinearLayout) m_activity.findViewById(android.R.id.content).getParent());
		setParent(s_content.getParent());
		if (s_menu == null) {
			LayoutInflater inflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			s_menu = inflater.inflate(R.layout.common_home_menu, null);
		}
	}

	private void setParent(final ViewParent viewParent) {
		if (s_parent != null) {
			removeView();
		}
		s_parent = (FrameLayout) viewParent;
	}

	public ListView getList() {
		return (ListView) s_menu.findViewById(R.id.menu_listview);
	}

	public Activity getCurrentActivity() {
		return m_activity;
	}

	// call this in your onCreate() for screen rotation
	public void checkEnabled() {
		LOG.verbose("checkEnabled()- menuShown: %s", s_menuShown);
		if (s_menuShown) {
			this.hide(false);
			this.show(false);
		}
	}

	public void show() {
		LOG.verbose("show()- menuShown: %s", s_menuShown);
		this.show(true);
	}

	public void show(final boolean animate) {
		LOG.verbose("show(%s)- menuShown: %s, parent: %s", animate, s_menuShown, s_parent);
		checkStatusBar();
		if (s_menuShown) {
			return;
		}
		s_menuShown = true;
		addView();
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) s_content.getLayoutParams();
		parm.setMargins(s_menuSize, 0, -s_menuSize, 0);
		s_content.setLayoutParams(parm);
		// animation for smooth slide-out
		if (animate) {
			TranslateAnimation taContent = new TranslateAnimation(-s_menuSize, 0, 0, 0);
			taContent.setDuration(DURATION);
			s_content.startAnimation(taContent);

			TranslateAnimation taMenu = new TranslateAnimation(-s_menuSize, 0, 0, 0);
			taMenu.setDuration(DURATION);
			s_menu.startAnimation(taMenu);
		}
		s_menu.findViewById(R.id.overlay).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				hide();
			}
		});
	}

	private void checkStatusBar() {
		Rect rect = new Rect();
		Window window = m_activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusHeight = rect.top;
		FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
		lays.setMargins(0, statusHeight, 0, 0);
		s_menu.setLayoutParams(lays);
	}

	public void hide() {
		LOG.verbose("hide()- menuShown: %s", s_menuShown);
		this.hide(true);
	}

	public void hide(final boolean animate) {
		LOG.verbose("hide(%s)- menuShown: %s, parent: %s", animate, s_menuShown, s_parent);
		if (!s_menuShown) {
			return;
		}
		s_menuShown = false;
		if (animate) {
			TranslateAnimation taMenu = new TranslateAnimation(0, -s_menuSize, 0, 0);
			taMenu.setDuration(DURATION);
			s_menu.startAnimation(taMenu);

			TranslateAnimation taContent = new TranslateAnimation(s_menuSize, 0, 0, 0);
			taContent.setDuration(DURATION);
			s_content.startAnimation(taContent);
		}
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) s_content.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		s_content.setLayoutParams(parm);
		removeView();
	}

	private void addView() {
		LOG.verbose("addView()");
		s_parent.addView(s_menu);
	}

	private void removeView() {
		LOG.verbose("removeView()");
		s_parent.removeView(s_menu);
	}
}
