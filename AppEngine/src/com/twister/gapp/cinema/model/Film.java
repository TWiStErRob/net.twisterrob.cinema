package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Film {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long edi;
	@Persistent
	private String title;
	@Persistent
	private int runtime;

	public Film(long edi, String title, int runtime) {
		this.edi = edi;
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
}
