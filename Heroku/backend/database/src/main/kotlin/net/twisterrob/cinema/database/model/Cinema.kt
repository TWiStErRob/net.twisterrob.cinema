package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.URIConverter
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.net.URI

@NodeEntity(label = "Cinema")
class Cinema : Historical() {

	@Property(name = "class")
	val className: String = "Cinema"

	@Property(name = "cineworldID")
	var cineworldID: Long = 0

	@Property(name = "name")
	lateinit var name: String

	@Property(name = "postcode")
	lateinit var postcode: String

	@Property(name = "address")
	lateinit var address: String

	@Property(name = "telephone")
	var telephone: String? = null

	@Convert(URIConverter::class)
	@Property(name = "cinema_url")
	lateinit var cinema_url: URI

	@Relationship(type = "GOESTO", direction = Relationship.INCOMING)
	var users: MutableCollection<User> = mutableSetOf()

	@Relationship(type = "AT", direction = Relationship.INCOMING)
	var views: MutableCollection<View> = mutableSetOf()

	override fun toString(): String =
		"Cinema[${graphId ?: "null"}](${cineworldID}, ${name})"

	fun copy(): Cinema {
		val copy = Cinema()
		copy.copyProperties(this)
		copy.cineworldID = cineworldID
		copy.name = name
		copy.postcode = postcode
		copy.address = address
		copy.telephone = telephone
		copy.cinema_url = cinema_url
		copy.users = users
		copy.views = views
		return copy
	}
}
