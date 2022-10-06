package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.listener.StoreCallback;

@PersistenceCapable
public class DateFieldsTestObject extends Dateable implements StoreCallback {

	/*@formatter:off*/ @Override public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	private String name;

	public DateFieldsTestObject(long id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
