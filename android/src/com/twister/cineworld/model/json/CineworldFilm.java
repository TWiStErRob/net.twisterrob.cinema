package com.twister.cineworld.model.json;

import com.google.gson.annotations.SerializedName;

// 3D: false
// advisory: ""
// classification: "15"
// edi: 42872
// film_url: "https://www.cineworld.co.uk/films/5409"
// id: 5409
// imax: false
// poster_url: "https://www.cineworld.co.uk/assets/media/films/5409_poster.jpg"
// still_url: "https://www.cineworld.co.uk/assets/media/films/5409_still.jpg"
// title: "Ted"
public class CineworldFilm {
	@SerializedName("3D")
	private boolean	m_3D;
	@SerializedName("advisory")
	private String	m_advisory;
	@SerializedName("classification")
	private String	m_classification;
	@SerializedName("edi")
	private long	m_edi;
	@SerializedName("film_url")
	private String	m_filmUrl;
	@SerializedName("id")
	private long	m_id;
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

	public String getAdvisory() {
		return m_advisory;
	}

	public String getClassification() {
		return m_classification;
	}

	public long getEdi() {
		return m_edi;
	}

	public String getFilmUrl() {
		return m_filmUrl;
	}

	public long getId() {
		return m_id;
	}

	public boolean isIMax() {
		return m_iMax;
	}

	public String getPosterUrl() {
		return m_posterUrl;
	}

	public String getStillUrl() {
		return m_stillUrl;
	}

	public String getTitle() {
		return m_title;
	}

}
