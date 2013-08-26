package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

import org.joda.time.DateTime;

@PersistenceCapable
public class Film {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long edi;
	@Persistent
	private String title;
	@Persistent
	private int runtime;

	@Persistent
	private DateTime created;
	@Persistent
	private DateTime lastUpdated;

	public Film(long edi, String title, int runtime) {
		if (edi <= 0) {
			throw new IllegalArgumentException("EDI must be positive");
		}
		this.edi = edi;
		this.title = title;
		this.runtime = runtime;
		this.setCreated(new DateTime());
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

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(DateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
