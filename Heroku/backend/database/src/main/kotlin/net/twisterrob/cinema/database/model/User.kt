package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import java.time.OffsetDateTime

@NodeEntity(label = "User")
class User(
	@Property(name = "id")
	var id: String,

	@Property(name = "name")
	var name: String,

	@Property(name = "email")
	var email: String,

	@Property(name = "realm")
	var realm: String,

	@Relationship(type = "GOESTO")
	var cinemas: MutableCollection<Cinema> = mutableSetOf(),

	@Relationship(type = "ATTENDED")
	var views: MutableCollection<View> = mutableSetOf()
) : BaseNode() {

	@Property(name = "class")
	val className: String = "User"

	@Property(name = "_created")
	lateinit var _created: OffsetDateTime

	override fun toString() = "User[$graphId]($id, $name)"
}

/*

@NodeEntity(label = "User")
class User(
	id: String,
	name: String,
	email: String,
	realm: String,
	_created: String
) {

	@Id
	@GeneratedValue
	private var graphId: Long = -1

	@Property(name = "id")
	var id: String = id
		private set

	@Property(name = "name")
	var name: String = name
		private set

	@Property(name = "email")
	var email: String = email
		private set

	@Property(name = "realm")
	var realm: String = realm
		private set

	@Property(name = "_created")
	var _created: String = _created
		private set

	@Relationship(type = "GOESTO")
	private lateinit var _cinemas: MutableSet<Cinema>
	val cinemas: Collection<Cinema> get() = _cinemas

	@Relationship(type = "ATTENDED")
	private lateinit var _views: MutableSet<View>
	val views: Collection<View> get() = _views

	override fun toString() = "User[$graphId]($id, $name)"
}

 */
