package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.DateLong
import java.time.Instant

@NodeEntity(label = "View")
class View : BaseNode() {

	@Property(name = "class")
	val className: String = "View"

	// TODO These don't work as expected because @Property is field-only.
//	@Property(name = "film")
//	val film: Long get() = watchedFilm.edi,
//
//	@Property(name = "cinema")
//	val cinema: Long get() = atCinema.cineworldID,
//
//	@Property(name = "user")
//	val user: String get() = userRef.id

	@Property(name = "date")
	@DateLong
	lateinit var date: Instant

	@Relationship(type = "AT", direction = Relationship.OUTGOING)
	lateinit var atCinema: Cinema

	@Relationship(type = "WATCHED", direction = Relationship.OUTGOING)
	lateinit var watchedFilm: Film

	@Relationship(type = "ATTENDED", direction = Relationship.INCOMING)
	lateinit var userRef: User

	override fun toString() =
		"View[$graphId](${userRef.name} watched ${watchedFilm.title} at ${atCinema.name}"
}
