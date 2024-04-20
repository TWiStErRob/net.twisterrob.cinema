package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.slf4j.LoggerFactory
import java.net.URI

private val LOG = LoggerFactory.getLogger(UriEncodingFixingUriDeserializer::class.java)

/**
 * Special-case to be able to parse this:
 * `https://www.cineworld.co.uk/films/scream-1997-[re-issue]` in [Feed.Film.url].
 *
 * It fails like this:
 * ```
 * com.fasterxml.jackson.databind.exc.InvalidFormatException:
 * Cannot deserialize value of type `java.net.URI` from String "https://www.cineworld.co.uk/films/scream-1997-[re-issue]":
 * not a valid textual representation, problem: Illegal character in path at index 46: https://www.cineworld.co.uk/films/scream-1997-[re-issue]
 * at [Source: (StringReader); line: 1252, column: 74]
 * at com.fasterxml.jackson.databind.exc.InvalidFormatException.from(InvalidFormatException.java:67)
 * at com.fasterxml.jackson.databind.DeserializationContext.weirdStringException(DeserializationContext.java:1991)
 * at com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.deserialize(FromStringDeserializer.java:173)
 * Caused by: java.lang.IllegalArgumentException: Illegal character in path at index 46: https://www.cineworld.co.uk/films/scream-1997-[re-issue]
 * at java.base/java.net.URI.create(URI.java:883)
 * at com.fasterxml.jackson.databind.deser.std.FromStringDeserializer$Std._deserialize(FromStringDeserializer.java:300)
 * at com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.deserialize(FromStringDeserializer.java:162)
 * ... 44 more
 * 		Caused by: java.net.URISyntaxException: Illegal character in path at index 46: https://www.cineworld.co.uk/films/scream-1997-[re-issue]
 * at java.base/java.net.URI$Parser.fail(URI.java:2915)
 * at java.base/java.net.URI$Parser.checkChars(URI.java:3086)
 * at java.base/java.net.URI$Parser.parseHierarchical(URI.java:3168)
 * at java.base/java.net.URI$Parser.parse(URI.java:3116)
 * at java.base/java.net.URI.<init>(URI.java:600)
 * at java.base/java.net.URI.create(URI.java:881)
 * ... 46 more
 * ```
 */
class UriEncodingFixingUriDeserializer : JsonDeserializer<URI>() {

	override fun deserialize(p: JsonParser, ctxt: DeserializationContext): URI {
		val de: JsonDeserializer<URI> = ctxt.findJdkDeserializer()
		@Suppress("LiftReturnOrAssignment")
		try {
			return de.deserialize(p, ctxt)
		} catch (ex: InvalidFormatException) {
			if ("Illegal character in path at index" in ex.message.orEmpty()) {
				/** @see com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.deserialize */
				val currentUri = p.valueAsString
				LOG.warn("Cannot parse URI: ${currentUri}", ex)
				// Try fixing URI by replacing invalid characters.
				val fixedUri = currentUri
					.replace("[", "%5B")
					.replace("]", "%5D")
				// Try again, this time directly, because p.assignCurrentValue doesn't work for valueAsString.
				/** @see com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.Std.STD_URI */
				return URI.create(fixedUri)
			} else {
				// Not the problem we're looking for, fail normally.
				throw ex
			}
		}
	}
}

private inline fun <reified T> DeserializationContext.findJdkDeserializer(): JsonDeserializer<T> =
	this.findJdkDeserializer(T::class.java, T::class.java.name)

private fun <T> DeserializationContext.findJdkDeserializer(rawType: Class<T>, clsName: String): JsonDeserializer<T> {
	val result = JdkDeserializers.find(this, rawType, clsName)
		?: error("Cannot find JDK deserializer for type ${rawType} and class name ${clsName}.")
	@Suppress("UNCHECKED_CAST") // Jackson find method is not generic.
	return result as JsonDeserializer<T>
}
