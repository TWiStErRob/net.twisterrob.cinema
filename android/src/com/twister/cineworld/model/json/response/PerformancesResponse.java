package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.*;

public class PerformancesResponse extends BaseResponse<CineworldPerformance> {
	@SerializedName("performances")
	private List<CineworldPerformance>	m_performances;

	@SerializedName("legends")
	private List<CineworldLegend>		m_legends;

	public List<CineworldPerformance> getPerformances() {
		return Collections.unmodifiableList(m_performances);
	}

	public List<CineworldLegend> getLegends() {
		return Collections.unmodifiableList(m_legends);
	}

	@Override
	public List<CineworldPerformance> getList() {
		return getPerformances();
	}
}
