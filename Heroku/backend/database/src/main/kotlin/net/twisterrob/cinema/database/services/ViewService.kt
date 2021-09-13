package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.database.model.View
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.queryForObject
import java.time.OffsetDateTime
import javax.inject.Inject

class ViewService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<View> =
		session.loadAll(View::class.java, 2)

	fun find(id: String): View? =
		session.load(View::class.java, id, 1)

	fun delete(id: String) {
		session.delete(session.load(View::class.java, id))
	}

	fun createOrUpdate(entity: View): View {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	/**
	 * Create a relationship node for "[user] watched [film] in [cinema] at [time]"
	 * @param cinema [Cinema.cineworldID]
	 * @param film [Film.edi]
	 * @param user [User.id]
	 * @param time [View.date]
	 */
	fun addView(user: String, film: Long, cinema: Long, time: OffsetDateTime): View? =
		session.queryForObject(
			"""
			MATCH
				(c:Cinema { cineworldID: ${"$"}cinemaID }),
				(f:Film { edi: ${"$"}filmEDI }),
				(u:User { id: ${"$"}userID })
			MERGE (u)-[t:ATTENDED]->(v:View {
				film: ${"$"}filmEDI,
				cinema: ${"$"}cinemaID,
				user: ${"$"}userID,
				date: ${"$"}dateEpochUTC
			})
			MERGE (v)-[a:AT]->(c)
			MERGE (v)-[w:WATCHED]->(f)
			// need to return nodes and relationships, otherwise OGM doesn't connect them
			RETURN v AS view, t AS attended, u AS user, a AS at, c AS cinema, w AS watched, f AS film
			""",
			mapOf(
				"userID" to user,
				"cinemaID" to cinema,
				"filmEDI" to film,
				"dateEpochUTC" to time.toInstant().toEpochMilli()
			)
		)

	/**
	 * Remove the [View]s for a given [film] for a [user] at [time] in [cinema].
	 * @param cinema [Cinema.cineworldID]
	 * @param film [Film.edi]
	 * @param user [User.id]
	 * @param time [View.date]
	 */
	fun removeView(user: String, film: Long, cinema: Long, time: OffsetDateTime): Unit = run {
		session.query(
			"""
			MATCH (v:View {date: ${"$"}dateEpochUTC })
			MATCH (v)<-[a:ATTENDED]-(u:User { id: ${"$"}userID })
			MATCH (v)-[w:WATCHED]->(f:Film { edi: ${"$"}filmEDI })
			MATCH (v)-[at:AT]->(c:Cinema { cineworldID: ${"$"}cinemaID })
			MATCH (v)-[r]-()
			DELETE v, r
			""",
			mapOf(
				"userID" to user,
				"cinemaID" to cinema,
				"filmEDI" to film,
				"dateEpochUTC" to time.toInstant().toEpochMilli()
			)
		)
	}
}
