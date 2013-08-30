package com.twister.gapp.cinema.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;

@PersistenceCapable
@Deprecated
public class TestDatesFields extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	private String name;

	public TestDatesFields(long id, String name) {
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
