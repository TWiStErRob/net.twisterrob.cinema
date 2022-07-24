package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

fun feedReader(): XmlMapper {
	val jackson = JacksonXmlModule().apply {
		setXMLTextElementName("innerText")
	}
	return XmlMapper(jackson).apply {
		registerModule(KotlinModule.Builder().build())
		registerModule(JavaTimeModule())
		// I want to know when new things are added to syndication.
		enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
	}
}

operator fun Feed.plus(other: Feed): Feed = Feed(
	// merge the attributes, keep only unique ones
	attributes = (this.attributes + other.attributes).distinctBy { it.code },
	// different country -> different cinemas
	cinemas = this.cinemas + other.cinemas,
	// both countries play the same movies
	films = (this.films + other.films).distinctBy { it.id },
	// performances are separate by cinemas, see cinemas above
	performances = this.performances + other.performances
)

/**
 * Jackson format for
 *  * [weekly_film_times.xml](https://www.cineworld.co.uk/syndication/weekly_film_times.xml)
 *  * [weekly_film_times_ie.xml](https://www.cineworld.co.uk/syndication/weekly_film_times_ie.xml)
 *
 * ## Tricky bits
 *
 * ### `@JacksonXmlText` in Kotlin
 * See https://github.com/FasterXML/jackson-module-kotlin/issues/138
 * `@JacksonXmlText` cannot be applied to constructor parameters, which is the only way to instantiate data classes.
 * ```
 * com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
 * Invalid definition for property `` (of type `net.twisterrob.cinema.cineworld.sync.syndication.Feed$Attribute`):
 * Could not find creator property with name '' (known Creator properties: [code, title])
 * at [Source: (BufferedInputStream); line: 2, column: 1]
 * ```
 * Tried many `@`[JsonAlias]`("")` and `@`[JsonProperty]`("title")` combinations, but same error.
 *
 * Luckily [JacksonXmlModule.setXMLTextElementName] works instead of annotating with [JacksonXmlText].
 * `@`[JacksonXmlText] is not necessary for deserialization,
 * but it's good to signal what `@`[JsonProperty]`(innerText)` is meant to represent and required for serialization.
 *
 * ### `@JsonIdentityInfo` in Kotlin
 * At the first instantiation of the object the ObjectId field is set as null,
 * and then later it's set via reflection, so it should be a `lateinit val`, which is not possible.
 * To work around this, the IDs are made nullable, but should never be null.
 */
@JacksonXmlRootElement(localName = "feed")
data class Feed(
	@JacksonXmlElementWrapper(localName = "attributes")
//	@JacksonXmlProperty(localName = "attribute")
	@JsonProperty(index = 1)
	val attributes: List<Attribute>,

	@JacksonXmlElementWrapper(localName = "cinemas")
//	@JacksonXmlProperty(localName = "cinema")
	@JsonProperty(index = 2)
	val cinemas: List<Cinema>,

	@JacksonXmlElementWrapper(localName = "films")
//	@JacksonXmlProperty(localName = "film")
	@JsonProperty(index = 3)
	val films: List<Film>,

	@JacksonXmlElementWrapper(localName = "performances")
//	@JacksonXmlProperty(localName = "screening")
	@JsonProperty(index = 4)
	val performances: List<Performance>,
) {

	constructor(
		attributes: List<Attribute>,
		performances: List<Performance>
	) : this(attributes, performances.map { it.cinema }, performances.map { it.film }, performances) {
		val attributeCodes = attributes.map { it.code }
		performances.forEach { performance ->
			performance.attributeList.forEach { attributeCode ->
				check(attributeCode in attributeCodes)
			}
		}
	}

	data class Attribute(
		/**
		 * @sample `"2D"`
		 * @sample `"gn:movies-for-juniors"`
		 */
		@JacksonXmlProperty(isAttribute = true)
		val code: String,

		/**
		 * @sample `"2D"`
		 * @sample `"Movies for Juniors"`
		 */
		@JacksonXmlProperty(localName = "innerText")
		@JacksonXmlText
		val title: String
	) {

		override fun toString() = "$code->$title"
	}

	@JsonIdentityInfo(
		scope = Cinema::class,
		generator = ObjectIdGenerators.PropertyGenerator::class,
		// TODEL https://twitter.com/TWiStErRob/status/1432281514511847433
		resolver = IgnoreDuplicatesObjectIdResolver::class,
		property = "id"
	)
	data class Cinema(
		/**
		 * @sample `"1"`
		 */
		@JacksonXmlProperty(isAttribute = true)
		val id: Long,

		/**
		 * @sample `"http://www1.cineworld.co.uk/cinemas/aberdeen-queens-links"`
		 */
		val url: URI,

		/**
		 * @sample `"Cineworld Aberdeen - Queens Links"`
		 */
		val name: String,

		/**
		 * @sample `"Queens Links Leisure Park, Links Road, Aberdeen"`
		 */
		val address: String,

		/**
		 * @sample `"AB24 5EN"`
		 */
		val postcode: String,

		/**
		 * Nullable: Leicester Square and Middlesbrough doesn't have a phone number.
		 * @sample `"0871 200 2000"`
		 */
		val phone: String?,

		/**
		 * @sample `"con,ns,park,vh,dba,3d,dbp,sb,hfr,etx,ad,dig,m4j,bas,bar,gdog,adv"`
		 */
		val services: String
	) {

		@JsonIgnore
		val serviceList = services.split(",")

		@Suppress("DataClassShouldBeImmutable")
		@JsonManagedReference("cinema")
		lateinit var performances: List<Performance>
			private set
	}

	@JsonIdentityInfo(
		scope = Film::class,
		generator = ObjectIdGenerators.PropertyGenerator::class,
		// TODEL https://twitter.com/TWiStErRob/status/1432281514511847433
		resolver = IgnoreDuplicatesObjectIdResolver::class,
		property = "id"
	)
	data class Film(
		/**
		 * @sample `"163254"`
		 */
		@JacksonXmlProperty(isAttribute = true)
		val id: Long,

		/**
		 * @sample `"Deadpool 2"`
		 */
		val title: String,

		/**
		 * @sample `"http://www1.cineworld.co.uk/films/deadpool-2"`
		 */
		@JsonDeserialize(using = UriEncodingFixingUriDeserializer::class)
		val url: URI,

		/**
		 * @sample `15`
		 * @sample `U`
		 */
		val classification: String,

		/**
		 * @sample `"2018-05-16T00:00:00Z"`
		 */
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T00:00:00Z'")
		val releaseDate: LocalDate,

		/**
		 * In minutes.
		 *
		 * @sample `120`
		 */
		val runningTime: Int,

		/**
		 * @sample `"David Leitch"`
		 */
		val director: String,

		/**
		 * @sample `"Brianna Hildebrand, Eddie Marsan, Josh Brolin, Morena Baccarin, Ryan Reynolds, T.J. Miller"`
		 */
		val cast: String,

		/**
		 * @sample `"long text ..."`
		 */
		val synopsis: String,

		/**
		 * @sample `"http://www1.cineworld.co.uk/xmedia-cw/repo/feats/posters/HO00005093.jpg"`
		 */
		val posterUrl: URI,

		/**
		 * Note: never seen it non-empty.
		 * @sample `""`
		 */
		val reasonToSee: String?,

		/**
		 * @sample `"ST,2D,AD,gn:action,gn:fantasy"`
		 */
		val attributes: String,

		/**
		 * @sample `"https://www.youtube.com/watch?v=45MzzFoEASQ"`
		 */
		val trailerUrl: URI?
	) {

		@JsonIgnore
		val attributeList = attributes.split(",")

		@Suppress("DataClassShouldBeImmutable")
		@JsonManagedReference("film")
		lateinit var performances: List<Performance>
			private set
	}

	data class Performance(
		/**
		 * @sample `"163254"`
		 */
		@JsonBackReference("film")
		@JacksonXmlProperty(isAttribute = true)
		val film: Film,

		/**
		 * @sample `"1"`
		 */
		@JsonBackReference("cinema")
		@JacksonXmlProperty(isAttribute = true)
		val cinema: Cinema,

		/**
		 * @sample `"https://booking.cineworld.co.uk/booking/8014/101536"`
		 */
		val url: URI,

		/**
		 * The time of screening this performance starts.
		 * Even though it's an ISO instant, the time is wrong.
		 * In UK DST (BST in Summer) a time is returned as `2018-07-21T10:30:00Z`,
		 * but this means "10:30" Cinema local time, so needs to be re-interpreted in [DEFAULT_TIMEZONE].
		 *
		 * @sample `"2018-07-21T10:00:00Z"`
		 */
		val date: OffsetDateTime,

		/**
		 * @sample `"2D,AD"`
		 */
		val attributes: String
	) {

		@JsonIgnore
		val attributeList = attributes.split(",")
	}

	companion object {

		/**
		 * Assume all the cinemas to be in the UK.
		 * This will get fun when Ireland has different DST rules.
		 */
		val DEFAULT_TIMEZONE: ZoneId = ZoneId.of("Europe/London")
	}
}
