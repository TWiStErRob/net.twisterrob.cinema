package com.twister.cineworld.model.json.data;

import com.google.gson.annotations.SerializedName;
import com.twister.cineworld.model.json.request.FilmsRequest;

/**
 * <p>
 * This query returns a list of films that have programmed performances. The results can be filtered by supplying
 * optional film, date and cinema parameters. These can all take multiple values, so for example it is possible to
 * search for all films showing at cinema1 on two specific dates. <br>
 * Calls to this part of the API will return films as well as other content Cineworld show in their screens such as
 * Operas.
 * </p>
 * <p>
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
 * 
 * </p>
 * 
 * @see FilmsRequest
 */
public class CineworldFilm extends CineworldBase {
	@SerializedName("edi")
	private int		m_edi;
	@SerializedName("title")
	private String	m_title;
	@SerializedName("id")
	private int		m_id;
	@SerializedName("classification")
	private String	m_classification;
	@SerializedName("advisory")
	private String	m_advisory;
	@SerializedName("poster_url")
	private String	m_posterUrl;
	@SerializedName("still_url")
	private String	m_stillUrl;
	@SerializedName("film_url")
	private String	m_filmUrl;
	@SerializedName("3D")
	private boolean	m_3D;
	@SerializedName("imax")
	private boolean	m_iMax;

	/**
	 * The EDI number used in responses are supplied by Nielsen-EDI for films, other content will have unique numbers
	 * not supplied by Nielsen-EDI.
	 * 
	 * @return EDI number
	 */
	public int getEdi() {
		return m_edi;
	}

	public void setEdi(final int edi) {
		m_edi = edi;
	}

	/**
	 * @return Film title
	 */
	public String getTitle() {
		return m_title;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

	/**
	 * @return Cineworld film id
	 */
	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	/**
	 * @return Film classification
	 */
	public String getClassification() {
		return m_classification;
	}

	public void setClassification(final String classification) {
		m_classification = classification;
	}

	/**
	 * @return Film consumer advisory text
	 */
	public String getAdvisory() {
		return m_advisory;
	}

	public void setAdvisory(final String advisory) {
		m_advisory = advisory;
	}

	/**
	 * @return Absolute URL to the film poster image
	 */
	public String getPosterUrl() {
		return m_posterUrl;
	}

	public void setPosterUrl(final String posterUrl) {
		m_posterUrl = posterUrl;
	}

	/**
	 * @return Absolute URL to the film still image
	 */
	public String getStillUrl() {
		return m_stillUrl;
	}

	public void setStillUrl(final String stillUrl) {
		m_stillUrl = stillUrl;
	}

	/**
	 * @return Absolute URL to film page
	 */
	public String getFilmUrl() {
		return m_filmUrl;
	}

	public void setFilmUrl(final String filmUrl) {
		m_filmUrl = filmUrl;
	}

	/**
	 * @return @undocumented
	 */
	public boolean is3D() {
		return m_3D;
	}

	public void set3D(final boolean is3D) {
		m_3D = is3D;
	}

	/**
	 * @return @undocumented
	 */
	public boolean isIMax() {
		return m_iMax;
	}

	public void setIMax(final boolean iMax) {
		m_iMax = iMax;
	}
}
