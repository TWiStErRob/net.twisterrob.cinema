package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.XmlElement;

@PersistenceCapable
public class Film extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	@XmlElement
	private String title;
	@Persistent
	@XmlElement
	private int runtime;

	public Film(long edi, String title, int runtime) {
		super(edi);
		if (edi <= 0) {
			throw new IllegalArgumentException("EDI must be positive");
		}
		this.title = title;
		this.runtime = runtime;
	}

	public long getEdi() {
		return getKeyId();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Film)) {
			return false;
		}
		Film other = (Film)obj;
		return getKey().equals(other.getKey());
	}
}
