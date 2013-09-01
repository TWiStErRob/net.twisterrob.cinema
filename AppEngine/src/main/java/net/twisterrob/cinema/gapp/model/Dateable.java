package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

import org.joda.time.DateTime;

import com.google.appengine.api.datastore.KeyFactory;

// TODO https://code.google.com/p/datanucleus-appengine-patch/
@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
// Don't store the class name as a discriminator value
@Discriminator(strategy = DiscriminatorStrategy.NONE)
// Do not serialize properties, just what's annotated
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Dateable implements StoreCallback {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@XmlElement(namespace = "http://appengine.google.com/datastore")
	private com.google.appengine.api.datastore.Key key;
	@Persistent
	@XmlElement(nillable = true)
	private DateTime created;
	@Persistent
	@XmlElement(nillable = true)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getKey() == null? super.hashCode() : getKey().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dateable) {
			if (this == obj) {
				return true;
			}
			Dateable other = (Dateable)obj;
			return this.getKey() == null? this == other : this.getKey().equals(other.getKey());
		} else { // obj !instanceof Dateable or obj is null
			return false;
		}
	}
}
