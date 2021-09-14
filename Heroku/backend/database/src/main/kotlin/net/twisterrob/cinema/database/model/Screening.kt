package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.TimestampConverter
import net.twisterrob.neo4j.ogm.URIConverter
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.net.URI
import java.time.OffsetDateTime

@NodeEntity(label = "Screening")
class Screening : BaseNode() {

	companion object

	@Property(name = "class")
	val className: String = "Screening"

	@Convert(URIConverter::class)
	@Property(name = "booking_url")
	lateinit var booking_url: URI

	@Convert(TimestampConverter::class)
	@Property(name = "time")
	lateinit var time: OffsetDateTime

	@Relationship(type = "AT", direction = Relationship.OUTGOING)
	lateinit var cinema: Cinema

	@Relationship(type = "SCREENS", direction = Relationship.OUTGOING)
	lateinit var film: Film

	override fun toString() =
		"Performance(cinema=${cinema.cineworldID}, film=${film.edi}, time=${time})"

	fun copy(): Screening {
		val copy = Screening()
		copy.booking_url = booking_url
		copy.time = time
		copy.cinema = cinema
		copy.film = film
		return copy
	}
}
