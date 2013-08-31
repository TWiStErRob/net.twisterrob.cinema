package com.twister.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	public LocalDate unmarshal(String v) throws Exception {
		return v == null? null : new LocalDate(v);
	}

	public String marshal(LocalDate v) throws Exception {
		return v == null? null : v.toString();
	}

}
