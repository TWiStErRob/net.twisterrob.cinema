package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

import org.joda.time.DateTime;

import com.google.appengine.api.datastore.KeyFactory;

// TODO https://code.google.com/p/datanucleus-appengine-patch/
@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Dateable implements StoreCallback {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@XmlElement(namespace = "http://google.com")
	private com.google.appengine.api.datastore.Key key;
	@Persistent
	@XmlElement
	private DateTime created;
	@Persistent
	@XmlElement
	private DateTime lastUpdated;

	public Dateable() {
		created = new DateTime();
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
		return key == null? null : key.getId();
	}
	public String getKeyName() {
		return key == null? null : key.getName();
	}

	public DateTime getCreated() {
		return created;
	}
	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(DateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public void jdoPreStore() {
		this.setLastUpdated(new DateTime());
	}
}
