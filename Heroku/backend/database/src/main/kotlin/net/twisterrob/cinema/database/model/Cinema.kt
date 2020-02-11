package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship

@NodeEntity(label = "Cinema")
class Cinema : Historical() {

	companion object

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

	@Property(name = "cinema_url")
	lateinit var cinema_url: String

	@Relationship(type = "GOESTO", direction = Relationship.INCOMING)
	var users: MutableCollection<User> = mutableSetOf()

	@Relationship(type = "AT", direction = Relationship.INCOMING)
	var views: MutableCollection<View> = mutableSetOf()

	override fun toString(): String {
		return String.format("Cinema(%d, %s)", cineworldID, name)
	}
}
