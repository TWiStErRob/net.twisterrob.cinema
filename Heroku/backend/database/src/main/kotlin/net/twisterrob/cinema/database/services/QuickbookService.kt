package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Screening
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.query
import java.time.LocalDate
import javax.inject.Inject

class QuickbookService @Inject constructor(
	private val session: Session
) {

	fun getFilmEDIs(date: LocalDate, cinemas: List<Long>): Iterable<Long> =
		session.query<Long>(
			"""
			MATCH (f:Film) WHERE f._deleted IS NULL
			MATCH (c:Cinema) WHERE c.cineworldID IN ${"$"}cinemaIDs
			MATCH (s:Screening) WHERE date.truncate('day', s.time) = ${"$"}date
			MATCH
				(s)-[AT]-(c),
				(s)-[SCREENS]-(f)
			RETURN
				DISTINCT f.edi
			""",
			mapOf(
				"date" to date,
				"cinemaIDs" to cinemas
			)
		)

	fun getScreenings(date: LocalDate, cinema: Long, film: Long): Iterable<Screening> =
		session.query<Screening>(
			"""
			MATCH (s:Screening) WHERE date.truncate('day', s.time) = ${"$"}date
			MATCH (f:Film) WHERE f.edi = ${"$"}film
			MATCH (c:Cinema) WHERE c.cineworldID = ${"$"}cinema
			MATCH
				(s)-[AT]-(c),
				(s)-[SCREENS]-(f)
			RETURN s
			""",
			mapOf(
				"date" to date,
				"cinema" to cinema,
				"film" to film,
			)
		)
}
