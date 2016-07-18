package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
@FetchGroup(name = "detailed", members = {@Persistent(name = "cinema")})
public class FavoriteCinema extends Dateable implements StoreCallback {
	/*@formatter:off*/ @Override public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent(mappedBy = "favoriteCinemas")
	@XmlElement
	private User user;
	@Persistent
	@Unowned
	@XmlElement
	private Cinema cinema;
	@Persistent
	@XmlAttribute
	private int displayOrder;
	@Persistent
	@XmlAttribute
	private short rating;

	public FavoriteCinema(User user, Cinema cinema, int displayOrder, short rating) {
		this.cinema = cinema;
		this.displayOrder = displayOrder;
		this.rating = rating;
		user.addFavoriteCinema(this);
	}

	public FavoriteCinema() {}

	@XmlAttribute
	public long getCinemaId() {
		return cinema != null? cinema.getId() : 0;
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

	public Cinema getCinema() {
		return cinema;
	}

	public void setCinema(Cinema cinema) {
		this.cinema = cinema;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public short getRating() {
		return rating;
	}

	public void setRating(short rating) {
		this.rating = rating;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cinema == null)? 0 : cinema.hashCode());
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
		if (!(obj instanceof FavoriteCinema)) {
			return false;
		}
		FavoriteCinema other = (FavoriteCinema)obj;
		if (cinema == null) {
			if (other.cinema != null) {
				return false;
			}
		} else if (!cinema.equals(other.cinema)) {
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
