package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldCategory;

public class CategoriesResponse extends BaseResponse<CineworldCategory> {
	@SerializedName("categories")
	private List<CineworldCategory>	m_categories;

	public List<CineworldCategory> getCategories() {
		return m_categories;
	}

	public void setCategories(final List<CineworldCategory> categories) {
		m_categories = categories;
	}

	@Override
	public List<CineworldCategory> getList() {
		return Collections.unmodifiableList(getCategories());
	}
}
