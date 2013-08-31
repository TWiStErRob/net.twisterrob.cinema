package com.twister.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

public class DateTimeAdapter extends XmlAdapter<String, DateTime> {

	public DateTime unmarshal(String v) throws Exception {
		return v == null? null : new DateTime(v);
	}

	public String marshal(DateTime v) throws Exception {
		return v == null? null : v.toString();
	}

}
