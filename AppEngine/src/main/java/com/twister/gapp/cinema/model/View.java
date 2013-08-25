package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

import org.joda.time.DateTime;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class View extends BaseEntity {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private com.google.appengine.api.datastore.Key key;
	@Persistent
	@Unowned
	private User user;
	@Persistent
	@Unowned
	private Film film;
	@Persistent
	private boolean seen;
	@Persistent
	private float relevant;

	// should be in BaseEntity
	@Persistent
	private DateTime created;
	@Persistent
	private DateTime lastUpdated;

	public View() {}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Film getFilm() {
		return film;
	}
	public void setFilm(Film film) {
		this.film = film;
	}
	public boolean isSeen() {
		return seen;
	}
	public void setSeen(boolean seen) {
		this.seen = seen;
	}
	public float getRelevant() {
		return relevant;
	}
	public void setRelevant(float relevant) {
		this.relevant = relevant;
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
