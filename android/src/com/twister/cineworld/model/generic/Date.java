package com.twister.cineworld.model.generic;

import java.util.Calendar;
import java.util.regex.*;

import com.twister.cineworld.log.*;

public class Date extends GenericBase {
	private static final CineworldLogger	LOG	= LogFactory.getLog(Tag.JSON);

	private String							m_date;							// TODO int or calendar or anything

	public String getDate() {
		return m_date;
	}

	public void setDate(final String date) {
		m_date = date;
	}

	public Calendar getCalendar() {
		Calendar calendar = null;
		if (getDate() != null) {
			Pattern pattern = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})");
			Matcher matcher = pattern.matcher(getDate());
			if (matcher.find()) {
				calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));
				calendar.set(Calendar.MONTH, Integer.parseInt(matcher.group(2)) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
				calendar.set(Calendar.HOUR, 0); // set 0 am in current time zone
				calendar.clear(Calendar.MINUTE); // not affected by time zone
				calendar.clear(Calendar.SECOND); // not affected by time zone
				calendar.clear(Calendar.MILLISECOND); // not affected by time zone
			} else {
				Date.LOG.warn("Invalid date: " + getDate());
			}
		} else {
			Date.LOG.warn("Empty date");
		}
		return calendar;
	}
}
