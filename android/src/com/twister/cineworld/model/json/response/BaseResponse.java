package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldBase;

public abstract class BaseResponse<T extends CineworldBase> {
	@SerializedName("m_errors")
	private List<String>	m_errors;

	public abstract List<T> getList();

	public List<String> getErrors() {
		return Collections.unmodifiableList(m_errors);
	}
}
