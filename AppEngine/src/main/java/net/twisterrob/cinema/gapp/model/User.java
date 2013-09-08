package net.twisterrob.cinema.gapp.model;

import java.util.*;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

@PersistenceCapable
public class User extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	@XmlElement
	private String email;
	@Persistent
	@XmlElement
	private String nickName;
	@Persistent(mappedBy = "user")
	@Element(dependent = "true", deleteAction = ForeignKeyAction.CASCADE)
	@XmlElement
	private Set<View> views;
	@Persistent(mappedBy = "user")
	@Element(dependent = "true", deleteAction = ForeignKeyAction.CASCADE)
	@XmlElement
	private Set<FavoriteCinema> favoriteCinemas;

	public User(String userId, String email, String nickName) {
		super(userId);
		this.email = email;
		this.nickName = nickName;
	}

	public String getId() {
		return getUserId();
	}

	@XmlAttribute
	public String getUserId() {
		return getKeyName();
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Set<View> getViews() {
		return Collections.unmodifiableSet(views);
	}
	public void addView(View view) {
		view.setUser(this);
		this.views.add(view);
	}
	public Set<FavoriteCinema> getFavoriteCinemas() {
		return Collections.unmodifiableSet(favoriteCinemas);
	}
	public void addFavoriteCinema(FavoriteCinema fav) {
		fav.setUser(this);
		this.favoriteCinemas.add(fav);
	}
}
