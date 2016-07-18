package net.twisterrob.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

public class DateTimeAdapter extends XmlAdapter<String, DateTime> {
	@Override
	public DateTime unmarshal(String v) {
		return v == null? null : new DateTime(v);
	}

	@Override
	public String marshal(DateTime v) {
		return v == null? null : v.toString();
	}
}
