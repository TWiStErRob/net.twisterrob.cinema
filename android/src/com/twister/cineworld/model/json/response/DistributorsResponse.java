package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldDistributor;

/**
 * <p>
 * Use this to retrieve an array of film distributors ordered by name and returned in JSON format. An example of what
 * will be returned is below:
 * 
 * <pre>
 * {"distributors":[{
 *     "id":31,
 *     "name":"Artificial Eye"
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
 * @see CineworldDistributor
 */
public class DistributorsResponse extends BaseListResponse<CineworldDistributor> {
	@SerializedName("distributors")
	private List<CineworldDistributor>	m_distributors;

	public List<CineworldDistributor> getDistributors() {
		return m_distributors;
	}

	public void setDistributors(final List<CineworldDistributor> distributors) {
		m_distributors = distributors;
	}

	@Override
	public List<CineworldDistributor> getList() {
		return Collections.unmodifiableList(getDistributors());
	}
}
