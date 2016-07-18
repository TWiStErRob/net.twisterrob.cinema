package net.twisterrob.utils.joda;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateMidnight;

/**
 * @deprecated {@link DateMidnight} is deprecated.
 * @see DateMidnight
 */
@Deprecated
@SuppressWarnings("deprecation")
public class DateMidnightAdapter extends XmlAdapter<String, DateMidnight> {
	@Override
	public DateMidnight unmarshal(String v) {
		return v == null? null : new DateMidnight(v);
	}

	@Override
	public String marshal(DateMidnight v) {
		return v == null? null : v.toString();
	}
}
