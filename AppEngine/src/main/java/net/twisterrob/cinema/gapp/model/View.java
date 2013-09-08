package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class View extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent(mappedBy = "views")
	@XmlElement
	private User user;
	@Persistent
	@Unowned
	@XmlElement
	private Film film;
	@Persistent
	@XmlElement
	private boolean seen;
	@Persistent
	@XmlElement
	private float relevant;

	public View() {}

	@XmlAttribute
	public long getFilmEdi() {
		return film != null? film.getEdi() : 0;
	}

	@XmlAttribute
	public String getUserId() {
		return user != null? user.getUserId() : null;
	}

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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((film == null)? 0 : film.hashCode());
		result = prime * result + ((user == null)? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof View)) {
			return false;
		}
		View other = (View)obj;
		if (film == null) {
			if (other.film != null) {
				return false;
			}
		} else if (!film.equals(other.film)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}
}
