package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import java.time.OffsetDateTime

@NodeEntity(label = "User")
class User : BaseNode() {

	companion object

	@Property(name = "class")
	val className: String = "User"

	@Id
	@Property(name = "id")
	lateinit var id: String

	@Property(name = "name")
	lateinit var name: String

	@Property(name = "email")
	lateinit var email: String

	@Property(name = "realm")
	lateinit var realm: String

	@Property(name = "_created")
	lateinit var _created: OffsetDateTime

	@Relationship(type = "GOESTO")
	var cinemas: MutableCollection<Cinema> = mutableSetOf()

	@Relationship(type = "ATTENDED")
	var views: MutableCollection<View> = mutableSetOf()

	override fun toString() =
		"User[$graphId]($id, $name)"
}
