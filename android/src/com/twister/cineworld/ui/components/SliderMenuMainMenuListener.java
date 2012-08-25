package com.twister.cineworld.ui.components;

import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.twister.cineworld.ui.model.MainMenuItem;

public class SliderMenuMainMenuListener implements OnItemClickListener {
	private SlideMenu	m_slider;

	public SliderMenuMainMenuListener(final SlideMenu slider) {
		m_slider = slider;
	}

	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		MainMenuItem item = (MainMenuItem) parent.getItemAtPosition(position);
		m_slider.hide();
		m_slider.getCurrentActivity().startActivity(item.getIntent());
	}
}
