package net.twisterrob.neo4j.ogm

import org.neo4j.ogm.typeconversion.AttributeConverter
import java.net.URI

class URIConverter : AttributeConverter<URI, String> {

	override fun toGraphProperty(value: URI?): String? = value?.toString()

	override fun toEntityAttribute(value: String?): URI? = value?.toURI()
}

// TODO workaround with replacement, make sure that data is always a valid URI during sync
private fun String.toURI(): URI =
	this
		// In DB: http://webcache1.bbccustomerpublishing.com/cineworld/trailers/The Wolf of Wall Street_qtp.mp4
		.replace(' ', '+')
		// In DB: https://www.cineworld.co.uk/films/the-italian-job-[1969]-film-classics
		.replace("[", "%5B")
		.replace("]", "%5D")
		.let { URI.create(it) }
