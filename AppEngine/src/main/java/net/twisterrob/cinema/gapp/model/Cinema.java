package net.twisterrob.cinema.gapp.model;

import javax.jdo.annotations.*;
import javax.jdo.listener.StoreCallback;
import javax.xml.bind.annotation.*;

@PersistenceCapable
public class Cinema extends Dateable implements StoreCallback {
	/*@formatter:off*/ @Override public void jdoPreStore() { super.jdoPreStore(); } /*@formatter:on*/// req'd hack to call super

	@Persistent
	@XmlElement
	private String name;
	@Persistent
	@XmlElement
	private String url;
	@Persistent
	@XmlElement
	private String address;
	@Persistent
	@XmlElement
	private String postcode;
	@Persistent
	@XmlElement
	private String telephone;

	public Cinema(int id, String name) {
		super(id);
		this.name = name;
	}

	@XmlAttribute
	public long getId() {
		return getKey().getId();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
