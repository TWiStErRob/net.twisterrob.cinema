package net.twisterrob.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
	@Override
	public LocalDateTime unmarshal(String v) {
		return v == null? null : new org.joda.time.DateTime(v).toLocalDateTime();
	}

	@Override
	public String marshal(LocalDateTime v) {
		return v == null? null : v.toString();
	}
}
