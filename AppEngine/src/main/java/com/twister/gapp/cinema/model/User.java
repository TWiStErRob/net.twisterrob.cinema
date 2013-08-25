package com.twister.gapp.cinema.model;

import java.util.List;

import javax.jdo.annotations.*;

import org.joda.time.DateTime;

@PersistenceCapable
public class User extends BaseEntity {
	@PrimaryKey
	@Persistent
	private String userId;
	@Persistent
	private String email;
	@Persistent
	private String nickName;
	@Persistent
	// @Unowned
	@Element(dependent = "true", deleteAction = ForeignKeyAction.CASCADE)
	private List<View> views;

	// should be in BaseEntity
	@Persistent
	private DateTime created;
	@Persistent
	private DateTime lastUpdated;

	public User(String userId, String email, String nickName) {
		this.userId = userId;
		this.email = email;
		this.nickName = nickName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public void addView(View view) {
		view.setUser(this);
		this.views.add(view);
	}

	public List<View> getViews() {
		return views;
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
