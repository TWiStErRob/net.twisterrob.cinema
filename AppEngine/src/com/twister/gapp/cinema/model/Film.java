package com.twister.gapp.cinema.model;

import java.util.List;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Film {
	// @Persistent
	// @Extension(vendorName = "datanucleus", key = "gae.pk-id", value = "true")
	private Long edi;
	@PrimaryKey
	private String key;
	@Persistent
	private String title;
	@Persistent
	private int runtime;
	@Persistent
	@Element(dependent = "true")
	private List<View> views;

	public Film(long edi, String title, int runtime) {
		this.edi = edi;
		this.key = String.valueOf(edi);
		this.title = title;
		this.runtime = runtime;
	}

	public long getEdi() {
		return edi;
	}

	public void setEdi(long edi) {
		this.edi = edi;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public void addView(View view) {
		view.setFilm(this);
		this.views.add(view);
	}

}
