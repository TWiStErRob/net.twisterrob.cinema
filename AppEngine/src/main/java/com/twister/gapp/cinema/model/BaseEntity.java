package com.twister.gapp.cinema.model;

import javax.jdo.listener.StoreCallback;

import org.joda.time.DateTime;

// the fields for created and lastUpdated should be here, but couldn't figure out the annotations yet
// InheritanceStrategy.COMPLETE_TABLE looked promising, but it's not in 3.0.1
public abstract class BaseEntity implements StoreCallback {
	public BaseEntity() {
		this.setCreated(new DateTime());
	}

	public abstract DateTime getCreated();

	public abstract void setCreated(DateTime created);

	public abstract DateTime getLastUpdated();

	public abstract void setLastUpdated(DateTime lastUpdated);

	@Override
	public void jdoPreStore() {
		DateTime now = new DateTime();
		System.out.printf("preStore(%s=%s): %s\n", getClass().getSimpleName(), this, now);
		// LOG.trace("Updating a(n) {}: {} -> {}", entity.getClass().getSimpleName(), entity.getLastUpdated(), now);
		this.setLastUpdated(now);
	}
}
