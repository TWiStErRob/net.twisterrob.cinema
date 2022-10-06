package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

@PersistenceCapable
public class Film extends Dateable implements StoreCallback {
	/*@formatter:off*/ @Override public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

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

	@XmlAttribute
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
}
