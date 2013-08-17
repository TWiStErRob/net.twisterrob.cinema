package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class View {
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
}
