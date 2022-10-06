package com.twister.cineworld.model.json.response;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.data.CineworldCategory;

/**
 * <p>
 * Use this to retrieve an array of unordered Cineworld film categories, returned in JSON format. An example of what
 * will be returned is below:
 * 
 * <pre>
 * {"categories":[{
 *     "code":"junior",
 *     "name":"Movies for Juniors"
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
 * @see CineworldCategory
 */
public class CategoriesResponse extends BaseListResponse<CineworldCategory> {
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
