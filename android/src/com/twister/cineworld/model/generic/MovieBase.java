package com.twister.cineworld.model.generic;

public abstract class MovieBase extends GenericBase {
	private static final long	serialVersionUID	= -6688494894494224704L;

	private String				m_title;
	private String				m_classification;
	private String				m_advisory;

	public String getTitle() {
		return m_title;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

	public String getClassification() {
		return m_classification;
	}

	public void setClassification(final String classification) {
		m_classification = classification;
	}

	public String getAdvisory() {
		return m_advisory;
	}

	public void setAdvisory(final String advisory) {
		m_advisory = advisory;
	}

	public abstract boolean has2D();

	public abstract boolean has3D();

	public abstract boolean hasIMax2D();

	public abstract boolean hasIMax3D();
}
