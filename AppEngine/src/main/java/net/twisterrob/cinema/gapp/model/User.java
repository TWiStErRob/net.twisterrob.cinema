package net.twisterrob.cinema.gapp.model;

import java.util.*;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.XmlElement;

@PersistenceCapable
public class User extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	@XmlElement
	private String email;
	@Persistent
	@XmlElement
	private String nickName;
	@Persistent
	@Element(dependent = "true", deleteAction = ForeignKeyAction.CASCADE)
	@XmlElement
	private List<View> views;

	public User(String userId, String email, String nickName) {
		super(userId);
		this.email = email;
		this.nickName = nickName;
	}

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

	public List<View> getViews() {
		return Collections.unmodifiableList(views);
	}
	public void addView(View view) {
		view.setUser(this);
		this.views.add(view);
	}
}
