package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldDistributor;

public class DistributorsResponse extends BaseResponse<CineworldDistributor> {
	@SerializedName("distributors")
	private List<CineworldDistributor>	m_distributors;

	public List<CineworldDistributor> getDistributors() {
		return Collections.unmodifiableList(m_distributors);
	}

	@Override
	public List<CineworldDistributor> getList() {
		return getDistributors();
	}
}
