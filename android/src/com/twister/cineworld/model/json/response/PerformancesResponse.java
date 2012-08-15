package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.*;

public class PerformancesResponse extends BaseListResponse<CineworldPerformance> {
	@SerializedName("performances")
	private List<CineworldPerformance>	m_performances;

	@SerializedName("legends")
	private List<CineworldLegend>	   m_legends;

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
