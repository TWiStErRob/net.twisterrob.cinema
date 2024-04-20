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

	@Property(name = "class")
	val className: String = "Performance"

	@Convert(URIConverter::class)
	@Property(name = "booking_url")
	lateinit var booking_url: URI

	@Property(name = "time")
	lateinit var time: ZonedDateTime

	@Relationship(type = "IN")
	lateinit var inCinema: Cinema

	@Relationship(type = "SCREENS")
	lateinit var screensFilm: Film

	override fun toString() =
		"Performance(cinema=${inCinema.cineworldID}, film=${screensFilm.edi}, time=${time})"

	fun copy(): Performance {
		val copy = Performance()
		copy.booking_url = booking_url
		copy.time = time
		copy.inCinema = inCinema
		copy.screensFilm = screensFilm
		return copy
	}
}
