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
	private static final CineworldLogger	LOG			= LogFactory.getLog(Tag.UI);
	private static boolean					s_menuShown	= false;
	private static View						s_menu;
	private static LinearLayout				s_content;
	private static FrameLayout				s_parent;
	private static int						s_menuSize;
	private Activity						m_activity;

	public SlideMenu(final Activity activity) {
		this.m_activity = activity;
		init();
	}

	private void init() {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("init()- menuShown: %s", SlideMenu.s_menuShown));
		}
		SlideMenu.s_menuSize = (int) (m_activity.getResources().getDisplayMetrics().widthPixels * 0.80f);
		SlideMenu.s_content = ((LinearLayout) m_activity.findViewById(android.R.id.content).getParent());
		setParent(SlideMenu.s_content.getParent());
		if (SlideMenu.s_menu == null) {
			LayoutInflater inflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			SlideMenu.s_menu = inflater.inflate(R.layout.common_home_menu, null);
		}
	}

	private void setParent(final ViewParent viewParent) {
		if (SlideMenu.s_parent != null) {
			removeView();
		}
		SlideMenu.s_parent = (FrameLayout) viewParent;
	}

	public ListView getList() {
		return (ListView) SlideMenu.s_menu.findViewById(R.id.menu_listview);
	}

	public Activity getCurrentActivity() {
		return m_activity;
	}

	// call this in your onCreate() for screen rotation
	public void checkEnabled() {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("checkEnabled()- menuShown: %s", SlideMenu.s_menuShown));
		}
		if (SlideMenu.s_menuShown) {
			this.hide(false);
			this.show(false);
		}
	}

	public void show() {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("show()- menuShown: %s", SlideMenu.s_menuShown));
		}
		this.show(true);
	}

	public void show(final boolean animate) {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("show(%s)- menuShown: %s, parent: %s", animate,
					SlideMenu.s_menuShown, SlideMenu.s_parent));
		}
		checkStatusBar();
		if (SlideMenu.s_menuShown) {
			return;
		}
		SlideMenu.s_menuShown = true;
		addView();
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) SlideMenu.s_content.getLayoutParams();
		parm.setMargins(SlideMenu.s_menuSize, 0, -SlideMenu.s_menuSize, 0);
		SlideMenu.s_content.setLayoutParams(parm);
		// animation for smooth slide-out
		if (animate) {
			TranslateAnimation ta = new TranslateAnimation(-SlideMenu.s_menuSize, 0, 0, 0);
			ta.setDuration(200);
			SlideMenu.s_content.startAnimation(ta);
			SlideMenu.s_menu.startAnimation(ta);
		}
		SlideMenu.s_menu.findViewById(R.id.overlay).setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				SlideMenu.this.hide();
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
		SlideMenu.s_menu.setLayoutParams(lays);
	}

	public void hide() {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("hide()- menuShown: %s", SlideMenu.s_menuShown));
		}
		this.hide(true);
	}

	public void hide(final boolean animate) {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("hide(%s)- menuShown: %s, parent: %s", animate,
					SlideMenu.s_menuShown, SlideMenu.s_parent));
		}
		if (!SlideMenu.s_menuShown) {
			return;
		}
		SlideMenu.s_menuShown = false;
		if (animate) {
			TranslateAnimation ta = new TranslateAnimation(0, -SlideMenu.s_menuSize, 0, 0);
			ta.setDuration(200);
			SlideMenu.s_menu.startAnimation(ta);

			TranslateAnimation tra = new TranslateAnimation(SlideMenu.s_menuSize, 0, 0, 0);
			tra.setDuration(200);
			SlideMenu.s_content.startAnimation(tra);
		}
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) SlideMenu.s_content.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		SlideMenu.s_content.setLayoutParams(parm);
		removeView();
	}

	private void addView() {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("addView()"));
		}
		SlideMenu.s_parent.addView(SlideMenu.s_menu);
	}

	private void removeView() {
		if (SlideMenu.LOG.isVerboseEnabled()) {
			SlideMenu.LOG.verbose(String.format("removeView()"));
		}
		SlideMenu.s_parent.removeView(SlideMenu.s_menu);
	}
}
