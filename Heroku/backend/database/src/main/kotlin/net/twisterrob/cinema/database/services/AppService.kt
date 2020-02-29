package net.twisterrob.cinema.database.services

import net.twisterrob.neo4j.ogm.queryForObject
import org.neo4j.ogm.session.Session
import javax.inject.Inject

class AppService @Inject constructor(
	private val session: Session
) {

	/**
	 * Dummy query to run at end of batch to get end event.
	 */
	fun endOfBatch() {
		val result = session.queryForObject<Unit>(
			"""
			START root=node(*)
			WHERE 1=2
			RETURN root
			"""
		)
		check(result == null)
	}
}
