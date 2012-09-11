package com.twister.cineworld.model.json.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldBase;

public abstract class BaseListResponse<T extends CineworldBase> {
	@SerializedName("m_errors")
	private List<String>	m_errors;

	public abstract List<T> getList();

	public List<String> getErrors() {
		return m_errors;
	}
}
