package net.twisterrob.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
	@Override
	public LocalTime unmarshal(String v) {
		return v == null? null : new LocalTime(v);
	}

	@Override
	public String marshal(LocalTime v) {
		return v == null? null : v.toString();
	}
}
