package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.listener.StoreCallback;

@PersistenceCapable
public class Cinema extends Dateable implements StoreCallback {
	/*@formatter:off*/ public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	private long id;
	private String name;
	private String url;
	private String address;
	private String postcode;
	private String telephone;
	private Vendor vendor;

	public Cinema() {}
}
