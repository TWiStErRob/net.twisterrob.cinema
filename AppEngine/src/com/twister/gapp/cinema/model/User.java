package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;

@PersistenceCapable
public class User {
	// @PrimaryKey
	// @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	// @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	// private String userId;
	// ->
	// Invalid primary key for com.twister.gapp.cinema.model.User. The primary key field is an encoded String but an
	// unencoded value has been provided. If you want to set an unencoded value on this field you can either
	// change its type to be an unencoded String (remove the "gae.encoded-pk" extension),
	// change its type to be a com.google.appengine.api.datastore.Key and then set the Key's name field,
	// or create a separate String field for the name component of your primary key and add the "gae.pk-name" extension.

	@Persistent
	private String email;
	@Persistent
	private String nickName;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String userId;

	public User(com.google.appengine.api.users.User authUser) {
		userId = authUser.getUserId();
		nickName = authUser.getNickname();
		email = authUser.getEmail();
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
