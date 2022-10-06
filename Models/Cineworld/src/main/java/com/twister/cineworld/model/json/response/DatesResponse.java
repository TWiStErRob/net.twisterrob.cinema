package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldDate;

/**
 * <p>
 * Use this to retrieve an array of dates (format yyyymmdd) that have performances ordered by date and returned in JSON
 * format. An example of what will be returned is below:
 * 
 * <pre>
 * {"dates":[
 *     "20091221",
 *     "20091222",
 *     "20091223",
 *     "20091224"
 * ]}
 * </pre>
 * 
 * </p>
 * <p>
 * If any errors should occur they will be returned as an array:
 * 
 * <pre>
 * {"errors":["valid key not supplied"]}
 * </pre>
 * 
 * </p>
 * 
 * @author papp.robert.s
 * @see CineworldDate
 */
public class DatesResponse extends BaseListResponse<CineworldDate> {
	@SerializedName("dates")
	private List<CineworldDate>	m_dates;

	public List<CineworldDate> getDates() {
		return m_dates;
	}

	public void setDates(final List<CineworldDate> dates) {
		m_dates = dates;
	}

	@Override
	public List<CineworldDate> getList() {
		return Collections.unmodifiableList(getDates());
	}
}
