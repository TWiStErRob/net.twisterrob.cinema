package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

@PersistenceCapable
public class User {
	@PrimaryKey
	@Persistent
	private String userId;
	@Persistent
	private String email;
	@Persistent
	private String nickName;

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
}
