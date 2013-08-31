package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
@XmlRootElement
public class View extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	@Unowned
	@XmlElement(nillable = true)
	private User user;
	@Persistent
	@Unowned
	@XmlElement(nillable = true)
	private Film film;
	@Persistent
	@XmlElement
	private boolean seen;
	@Persistent
	@XmlElement
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
