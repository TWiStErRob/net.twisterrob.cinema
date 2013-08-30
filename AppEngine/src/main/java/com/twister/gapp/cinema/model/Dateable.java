package com.twister.gapp.cinema.model;

import java.util.Date;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;

import com.google.appengine.api.datastore.KeyFactory;

// TODO https://code.google.com/p/datanucleus-appengine-patch/
@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class Dateable implements StoreCallback {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private com.google.appengine.api.datastore.Key key;
	@Persistent
	private Date created;
	@Persistent
	private Date lastUpdated;

	public Dateable() {
		created = new Date();
	}
	public Dateable(long id) {
		this();
		key = KeyFactory.createKey(getClass().getSimpleName(), id);
	}
	public Dateable(String name) {
		this();
		key = KeyFactory.createKey(getClass().getSimpleName(), name);
	}

	public com.google.appengine.api.datastore.Key getKey() {
		return key;
	}
	public Long getKeyId() {
		return key.getId();
	}
	public String getKeyName() {
		return key.getName();
	}

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public void jdoPreStore() {
		this.setLastUpdated(new Date());
	}
}
