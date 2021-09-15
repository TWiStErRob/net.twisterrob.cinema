package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Performance
import net.twisterrob.neo4j.ogm.deleteForCount
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.query
import java.time.LocalDate
import javax.inject.Inject

class PerformanceService @Inject constructor(
	private val session: Session
) {

	fun deleteAll(): Long =
		session.deleteForCount<Performance>()

	fun save(list: List<Performance>) {
		session.save(list)
	}

	fun getPerformances(date: LocalDate, cinemaIDs: List<Long>, filmEDIs: List<Long>): Iterable<Performance> =
		session.query<Performance>(
			"""
			MATCH (p:Performance) WHERE date.truncate('day', p.time) = ${"$"}date
			MATCH (f:Film) WHERE f.edi IN ${"$"}films
			MATCH (c:Cinema) WHERE c.cineworldID IN ${"$"}cinemas
			MATCH
				(p)-[in:IN]->(c),
				(p)-[s:SCREENS]->(f)
			RETURN p AS performance,
				in AS in, c AS cinema,
				s AS screens, f AS film 
			""",
			mapOf(
				"date" to date,
				"cinemas" to cinemaIDs,
				"films" to filmEDIs,
			)
		)
}
