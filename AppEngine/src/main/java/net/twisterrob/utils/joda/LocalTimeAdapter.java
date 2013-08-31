package net.twisterrob.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

	public LocalTime unmarshal(String v) throws Exception {
		return v == null? null : new LocalTime(v);
	}

	public String marshal(LocalTime v) throws Exception {
		return v == null? null : v.toString();
	}

}
