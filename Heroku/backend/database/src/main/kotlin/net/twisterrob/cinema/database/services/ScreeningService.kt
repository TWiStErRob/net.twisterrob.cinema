package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Screening
import net.twisterrob.neo4j.ogm.deleteForCount
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.query
import java.time.LocalDate
import javax.inject.Inject

class ScreeningService @Inject constructor(
	private val session: Session
) {

	fun deleteAll(): Long =
		session.deleteForCount<Screening>()

	fun save(list: List<Screening>) {
		session.save(list)
	}

	fun getScreenings(date: LocalDate, cinemaIDs: List<Long>, filmEDIs: List<Long>): Iterable<Screening> =
		session.query<Screening>(
			"""
			MATCH (s:Screening) WHERE date.truncate('day', s.time) = ${"$"}date
			MATCH (f:Film) WHERE f.edi IN ${"$"}films
			MATCH (c:Cinema) WHERE c.cineworldID IN ${"$"}cinemas
			MATCH
				(s)-[AT]-(c),
				(s)-[SCREENS]-(f)
			RETURN s, c, f
			""",
			mapOf(
				"date" to date,
				"cinemas" to cinemaIDs,
				"films" to filmEDIs,
			)
		)
}
