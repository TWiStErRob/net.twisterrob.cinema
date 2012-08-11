package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * Example JSON:
 * 
 * <pre>
 * {
 * code: "reg",
 * name: "Regular"
 * }
 * </pre>
 * 
 * Possible performance type codes are:
 * <ul>
 * <li>reg - Regular</li>
 * <li>vip - VIP</li>
 * <li>del - Delux</li>
 * <li>digital - Digital</li>
 * <li>m4j - Movies for Juniors</li>
 * <li>dbox - D-Box</li>
 * </ul>
 */
public class CineworldLegend extends CineworldBase {
	@SerializedName("code")
	private String	m_code;
	@SerializedName("name")
	private String	m_name;

	public String getCode() {
		return m_code;
	}

	public void setCode(final String code) {
		m_code = code;
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}
}
