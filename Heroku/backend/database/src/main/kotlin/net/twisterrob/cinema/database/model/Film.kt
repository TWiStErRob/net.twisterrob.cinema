package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.URIConverter
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.net.URI
import java.time.OffsetDateTime

@NodeEntity(label = "Film")
class Film : Historical() {

	companion object

	@Property(name = "class")
	val className: String = "Film"

	@Property(name = "edi")
	var edi: Long = 0

	@Property(name = "cineworldID")
	var cineworldID: Long? = null

	@Property(name = "cineworldInternalID")
	var cineworldInternalID: Long = 0

	@Property(name = "title")
	lateinit var title: String

	@Property(name = "originalTitle")
	lateinit var originalTitle: String

	@Property(name = "advisory")
	var advisory: String? = null

	@Property(name = "classification")
	lateinit var classification: String

	@Property(name = "cert")
	lateinit var cert: String

	@Property(name = "actors")
	lateinit var actors: String

	@Property(name = "director")
	lateinit var director: String

	@Property(name = "imax")
	var imax: Boolean = false

	@Property(name = "3D")
	var `3D`: Boolean = false

	@Property(name = "runtime")
	var runtime: Long = 0

	@Property(name = "weighted")
	var weighted: Long = 0

	@Property(name = "slug")
	lateinit var slug: String

	@Property(name = "group")
	var group: Long = 0

	@Property(name = "format")
	lateinit var format: String

	@Convert(URIConverter::class)
	@Property(name = "still_url")
	var still_url: URI? = null

	@Convert(URIConverter::class)
	@Property(name = "film_url")
	lateinit var film_url: URI

	@Convert(URIConverter::class)
	@Property(name = "poster_url")
	lateinit var poster_url: URI

	@Property(name = "poster")
	var poster: String? = null

	@Convert(URIConverter::class)
	@Property(name = "trailer")
	var trailer: URI? = null

	@Property(name = "release")
	lateinit var release: OffsetDateTime

	@Property(name = "categories")
	var categories: List<String> = mutableListOf()

	@Relationship(type = "WATCHED", direction = Relationship.INCOMING)
	var views: MutableCollection<View> = mutableSetOf()

	override fun toString() =
		"Film[$graphId](${cineworldID}, ${title})"

	fun copy(): Film {
		val copy = Film()
		copy.copyProperties(this)
		copy.edi = edi
		copy.cineworldID = cineworldID
		copy.cineworldInternalID = cineworldInternalID
		copy.title = title
		copy.originalTitle = originalTitle
		copy.advisory = advisory
		copy.classification = classification
		copy.cert = cert
		copy.actors = actors
		copy.director = director
		copy.imax = imax
		copy.`3D` = `3D`
		copy.runtime = runtime
		copy.weighted = weighted
		copy.slug = slug
		copy.group = group
		copy.format = format
		copy.still_url = still_url
		copy.film_url = film_url
		copy.poster_url = poster_url
		copy.poster = poster
		copy.trailer = trailer
		copy.release = release
		copy.categories = categories
		copy.views = views
		return copy
	}
}
