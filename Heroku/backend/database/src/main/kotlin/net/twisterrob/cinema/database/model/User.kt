package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.TimestampConverter
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.time.OffsetDateTime

@NodeEntity(label = "User")
class User : BaseNode() {

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

	@Suppress("ObjectPropertyNaming")
	@Convert(TimestampConverter::class)
	@Property(name = "_created")
	lateinit var _created: OffsetDateTime

	@Relationship(type = "GOESTO")
	var cinemas: MutableCollection<Cinema> = mutableSetOf()

	@Relationship(type = "ATTENDED")
	var views: MutableCollection<View> = mutableSetOf()

	override fun toString(): String =
		"User[${graphId ?: "null"}]($id, $name)"

	fun copy(): User {
		val copy = User()
		copy.copyProperties(this)
		copy.id = id
		copy.name = name
		copy.email = email
		copy.realm = realm
		copy._created = _created
		copy.cinemas = cinemas
		copy.views = views
		return copy
	}
}
