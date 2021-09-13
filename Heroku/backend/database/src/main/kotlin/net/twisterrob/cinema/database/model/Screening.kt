package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.URIConverter
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.net.URI

@NodeEntity(label = "Performance")
class Screening : BaseNode() {

	companion object

	@Property(name = "class")
	val className: String = "Performance"

	@Convert(URIConverter::class)
	@Property(name = "booking_url")
	lateinit var booking_url: URI

	@Relationship(type = "AT", direction = Relationship.OUTGOING)
	lateinit var cinema: Cinema

	@Relationship(type = "SCREENS", direction = Relationship.OUTGOING)
	lateinit var film: Film

	override fun toString() =
		"Performance(STOPSHIP}"

	fun copy(): Screening {
		val copy = Screening()
		copy.booking_url = booking_url
		copy.cinema = cinema
		copy.film = film
//		copy.address = address
//		copy.telephone = telephone
//		copy.cinema_url = cinema_url
//		copy.users = users
//		copy.views = views
		return copy
	}
}
