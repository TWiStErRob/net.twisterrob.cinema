package com.twister.cineworld.ui.components;

import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.twister.cineworld.log.*;
import com.twister.cineworld.ui.model.MainMenuItem;

public class SliderMenuMainMenuListener implements OnItemClickListener {
	private static final Log	LOG	= LogFactory.getLog(Tag.UI);

	private SlideMenu			m_slider;

	public SliderMenuMainMenuListener(final SlideMenu slider) {
		m_slider = slider;
	}

	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		LOG.verbose("onItemClick");
		MainMenuItem item = (MainMenuItem) parent.getItemAtPosition(position);
		m_slider.hide(false);
		m_slider.getCurrentActivity().startActivity(item.getIntent());
	}
}
