package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldDistributor;

public class DistributorsResponse extends BaseResponse<CineworldDistributor> {
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
