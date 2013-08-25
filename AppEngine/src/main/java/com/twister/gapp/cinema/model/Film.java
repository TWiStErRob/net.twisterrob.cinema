package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

import org.joda.time.DateTime;

@PersistenceCapable
public class Film extends BaseEntity {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long edi;
	@Persistent
	private String title;
	@Persistent
	private int runtime;

	// should be in BaseEntity
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

	@Override
	// should be in BaseEntity
	public DateTime getCreated() {
		return created;
	}

	@Override
	// should be in BaseEntity
	public void setCreated(DateTime created) {
		this.created = created;
	}

	@Override
	// should be in BaseEntity
	public DateTime getLastUpdated() {
		return lastUpdated;
	}

	@Override
	// should be in BaseEntity
	public void setLastUpdated(DateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
