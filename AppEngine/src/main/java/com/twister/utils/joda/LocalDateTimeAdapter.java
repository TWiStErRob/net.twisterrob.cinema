package com.twister.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	public LocalDateTime unmarshal(String v) throws Exception {
		return v == null? null : new org.joda.time.DateTime(v).toLocalDateTime();
	}

	public String marshal(LocalDateTime v) throws Exception {
		return v == null? null : v.toString();
	}

}
