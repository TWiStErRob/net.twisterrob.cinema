package net.twisterrob.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
	@Override
	public LocalDate unmarshal(String v) {
		return v == null? null : new LocalDate(v);
	}

	@Override
	public String marshal(LocalDate v) {
		return v == null? null : v.toString();
	}
}
