package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Screening
import org.neo4j.ogm.session.Session
import javax.inject.Inject

class ScreeningService @Inject constructor(
	private val session: Session
) {

	fun deleteAll() {
		session.deleteAll(Screening::class.java)
	}

	fun save(list: List<Screening>) {
		session.save(list)
	}
}
