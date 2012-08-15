package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;

/**
 * Example JSON:
 * 
 * <pre>
 * {
 * 3D: false,
 * advisory: "",
 * classification: "15",
 * edi: 42872,
 * film_url: "https://www.cineworld.co.uk/films/5409",
 * id: 5409,
 * imax: false,
 * poster_url: "https://www.cineworld.co.uk/assets/media/films/5409_poster.jpg",
 * still_url: "https://www.cineworld.co.uk/assets/media/films/5409_still.jpg",
 * title: "Ted"
 * }
 * </pre>
 */
public class CineworldFilm extends CineworldBase {
	@SerializedName("3D")
	private boolean	m_3D;
	@SerializedName("advisory")
	private String	m_advisory;
	@SerializedName("classification")
	private String	m_classification;
	@SerializedName("edi")
	private int	    m_edi;
	@SerializedName("film_url")
	private String	m_filmUrl;
	@SerializedName("id")
	private int	    m_id;
	@SerializedName("imax")
	private boolean	m_iMax;
	@SerializedName("poster_url")
	private String	m_posterUrl;
	@SerializedName("still_url")
	private String	m_stillUrl;
	@SerializedName("title")
	private String	m_title;

	public boolean is3D() {
		return m_3D;
	}

	public void set3D(final boolean is3D) {
		m_3D = is3D;
	}

	public String getAdvisory() {
		return m_advisory;
	}

	public void setAdvisory(final String advisory) {
		m_advisory = advisory;
	}

	public String getClassification() {
		return m_classification;
	}

	public void setClassification(final String classification) {
		m_classification = classification;
	}

	public int getEdi() {
		return m_edi;
	}

	public void setEdi(final int edi) {
		m_edi = edi;
	}

	public String getFilmUrl() {
		return m_filmUrl;
	}

	public void setFilmUrl(final String filmUrl) {
		m_filmUrl = filmUrl;
	}

	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	public boolean isIMax() {
		return m_iMax;
	}

	public void setIMax(final boolean iMax) {
		m_iMax = iMax;
	}

	public String getPosterUrl() {
		return m_posterUrl;
	}

	public void setPosterUrl(final String posterUrl) {
		m_posterUrl = posterUrl;
	}

	public String getStillUrl() {
		return m_stillUrl;
	}

	public void setStillUrl(final String stillUrl) {
		m_stillUrl = stillUrl;
	}

	public String getTitle() {
		return m_title;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

}
