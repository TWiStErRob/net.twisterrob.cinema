package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.*;

/**
 * <p>
 * Use this to retrieve an array of Cineworld performances ordered by time and returned in JSON format. An example of
 * what will be returned is below:
 * 
 * <pre>
 * {"performances":[{
 *     "time":"12:30",
 *     "available":false,
 *     "type":"reg",
 *     "booking_url":"http://www.cineworld.co.uk/booking?performance=18697&key=aabbcc"
 * }]}
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
 * @see CineworldPerformance
 */
public class PerformancesResponse extends BaseListResponse<CineworldPerformance> {
	@SerializedName("performances")
	private List<CineworldPerformance>	m_performances;

	@SerializedName("legends")
	private List<CineworldLegend>		m_legends;

	public List<CineworldPerformance> getPerformances() {
		return m_performances;
	}

	public void setPerformances(final List<CineworldPerformance> performances) {
		m_performances = performances;
	}

	public List<CineworldLegend> getLegends() {
		return m_legends;
	}

	public void setLegends(final List<CineworldLegend> legends) {
		m_legends = legends;
	}

	@Override
	public List<CineworldPerformance> getList() {
		return Collections.unmodifiableList(getPerformances());
	}
}
