package net.twisterrob.cinema.database.model

import net.twisterrob.neo4j.ogm.URIConverter
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.net.URI
import java.time.ZonedDateTime

@NodeEntity(label = "Performance")
class Performance : BaseNode() {

	companion object

	@Property(name = "class")
	val className: String = "Performance"

	@Convert(URIConverter::class)
	@Property(name = "booking_url")
	lateinit var booking_url: URI

	@Property(name = "time")
	lateinit var time: ZonedDateTime

	@Relationship(type = "AT", direction = Relationship.OUTGOING)
	lateinit var atCinema: Cinema

	@Relationship(type = "SCREENS", direction = Relationship.OUTGOING)
	lateinit var screensFilm: Film

	override fun toString() =
		"Performance(cinema=${atCinema.cineworldID}, film=${screensFilm.edi}, time=${time})"

	fun copy(): Performance {
		val copy = Performance()
		copy.booking_url = booking_url
		copy.time = time
		copy.atCinema = atCinema
		copy.screensFilm = screensFilm
		return copy
	}
}
