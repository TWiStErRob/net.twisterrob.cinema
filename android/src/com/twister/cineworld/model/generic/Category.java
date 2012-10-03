package com.twister.cineworld.model.generic;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.request.CategoriesRequest;

/**
 * <p>
 * This query returns a list of film categories used by cineworld. Categories are used to conveniently group films
 * together. The API will only return 'simple' categories, such as 'Movies for Juniors', but not dynamically calculated
 * categories, such as 'Now Showing'.
 * </p>
 * <p>
 * Example JSON:
 * 
 * <pre>
 * {
 * code: "junior",
 * name: "Movies for Juniors"
 * }
 * </pre>
 * 
 * </p>
 * 
 * @see CategoriesRequest
 */
public class Category extends GenericBase {
	@SerializedName("code")
	private String	m_code;
	@SerializedName("name")
	private String	m_name;

	/**
	 * @return System code for the category. This is the value that should be used by when making calls to the API that
	 *         accept a category value
	 */
	public String getCode() {
		return m_code;
	}

	public void setCode(final String code) {
		m_code = code;
	}

	/**
	 * @return Full category name
	 */
	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}
}
