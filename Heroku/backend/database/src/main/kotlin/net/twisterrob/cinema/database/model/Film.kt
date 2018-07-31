package net.twisterrob.cinema.database.model

import net.twisterrob.kotlin.getValue
import net.twisterrob.kotlin.setValue
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import java.time.OffsetDateTime

@NodeEntity(label = "Film")
class Film(
	@Property(name = "class")
	private var `class`: String = "Film"
) : Historical() {

	companion object

	var className: String by ::`class`

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
	@Property(name = "still_url")
	var still_url: String? = null
	@Property(name = "film_url")
	lateinit var film_url: String
	@Property(name = "poster_url")
	lateinit var poster_url: String
	@Property(name = "poster")
	var poster: String? = null
	@Property(name = "trailer")
	var trailer: String? = null

	@Property(name = "release")
	lateinit var release: OffsetDateTime

	@Property(name = "categories")
	lateinit var categories: List<String>

	//	@Relationship(type = "WATCHED", direction = Relationship.INCOMING)
	//	private lateinit var _views: MutableSet<View>
	//	val views: Collection<View> get() = _views
	override fun toString(): String {
		return String.format("Film(%d, %s)", cineworldID, title)
	}
}
