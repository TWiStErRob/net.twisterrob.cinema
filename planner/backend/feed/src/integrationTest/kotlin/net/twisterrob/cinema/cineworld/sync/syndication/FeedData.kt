package net.twisterrob.cinema.cineworld.sync.syndication

import org.junit.jupiter.api.Assertions.assertTrue

object FeedData {

	val SCREENING_TYPES: Set<Feed.Attribute> = setOf(
		Feed.Attribute(code = "2D", title = "2D"),
		Feed.Attribute(code = "3D", title = "3D"),
		Feed.Attribute(code = "4DX", title = "4DX"),
		Feed.Attribute(code = "AC", title = "AC"),
		Feed.Attribute(code = "AD", title = "AD"),
		Feed.Attribute(code = "AUT", title = "AUT"),
		Feed.Attribute(code = "Box", title = "Box"),
		Feed.Attribute(code = "CH", title = "CH"),
		Feed.Attribute(code = "CINB", title = "CINB"),
		Feed.Attribute(code = "DBOX", title = "DBOX"),
		Feed.Attribute(code = "EDU", title = "EDU"),
		Feed.Attribute(code = "FEV", title = "FEV"),
		Feed.Attribute(code = "IMAX", title = "IMAX"),
		Feed.Attribute(code = "M4J", title = "M4J"),
		Feed.Attribute(code = "MID", title = "MID"),
		Feed.Attribute(code = "PRE", title = "PRE"),
		Feed.Attribute(code = "QA", title = "QA"),
		Feed.Attribute(code = "SC", title = "SC"),
		Feed.Attribute(code = "Sen", title = "Sen"),
		Feed.Attribute(code = "SKY", title = "SKY"),
		Feed.Attribute(code = "SS", title = "SS"),
		Feed.Attribute(code = "ST", title = "ST"),
		Feed.Attribute(code = "STAR", title = "STAR"),
		Feed.Attribute(code = "Strobe", title = "Strobe"),
		Feed.Attribute(code = "TS", title = "TS"),
		Feed.Attribute(code = "ViP", title = "ViP"),
		Feed.Attribute(code = "VIP", title = "VIP")
	)

	val GENRES: Set<Feed.Attribute> = setOf(
		Feed.Attribute(code = "gn:action", title = "Action"),
		Feed.Attribute(code = "gn:animation", title = "Animation"),
		Feed.Attribute(code = "gn:bollywood", title = "Bollywood"),
		Feed.Attribute(code = "gn:comedy", title = "Comedy"),
		Feed.Attribute(code = "gn:documentary", title = "Documentary"),
		Feed.Attribute(code = "gn:drama", title = "Drama"),
		Feed.Attribute(code = "gn:event-cinema", title = "Event Cinema"),
		Feed.Attribute(code = "gn:family", title = "Family"),
		Feed.Attribute(code = "gn:fantasy", title = "Fantasy"),
		Feed.Attribute(code = "gn:hindi", title = "Hindi"),
		Feed.Attribute(code = "gn:horror", title = "Horror"),
		Feed.Attribute(code = "gn:live", title = "Live"),
		Feed.Attribute(code = "gn:malayalam", title = "Malayalam"),
		Feed.Attribute(code = "gn:movies-for-juniors", title = "Movies for Juniors"),
		Feed.Attribute(code = "gn:musical", title = "Musical"),
		Feed.Attribute(code = "gn:national-theatre", title = "National Theatre"),
		Feed.Attribute(code = "gn:polish", title = "Polish"),
		Feed.Attribute(code = "gn:punjabi", title = "Punjabi"),
		Feed.Attribute(code = "gn:romance", title = "Romance"),
		Feed.Attribute(code = "gn:sci-fi", title = "Sci-Fi"),
		Feed.Attribute(code = "gn:tamil", title = "Tamil"),
		Feed.Attribute(code = "gn:telugu", title = "Telugu"),
		Feed.Attribute(code = "gn:theatre", title = "Theatre"),
		Feed.Attribute(code = "gn:thriller", title = "Thriller"),
		Feed.Attribute(code = "gn:unlimited-screening", title = "Unlimited Screening")
	)
}

fun Feed.verifyHasAllAttributes(attributes: Set<Feed.Attribute>) {
	attributes.forEach { attribute ->
		assertTrue(this.attributes.contains(attribute)) {
			"$attribute not found in ${this.attributes}"
		}
	}
}
