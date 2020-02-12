package net.twisterrob.neo4j.ogm

import org.neo4j.ogm.typeconversion.AttributeConverter
import java.net.URI

class URIConverter : AttributeConverter<URI, String> {

	override fun toGraphProperty(value: URI?): String? = value?.toString()

	override fun toEntityAttribute(value: String?): URI? = value?.toURI()
}

private fun String.toURI(): URI = URI.create(this)
