package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Cinema
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.query
import javax.inject.Inject

class CinemaService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Cinema> =
		session.loadAll(Cinema::class.java, 0)

	fun find(id: String): Cinema? =
		session.load(Cinema::class.java, id, 1)

	fun delete(id: String) =
		delete(session.load(Cinema::class.java, id))

	fun delete(entity: Cinema) =
		session.delete(entity)

	fun createOrUpdate(entity: Cinema): Cinema {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	fun save(list: List<Cinema>) =
		session.save(list)

	/**
	 * Get all the Cinemas which are active.
	 */
	fun getActiveCinemas(): Iterable<Cinema> = session.query<Cinema>(
		"""
		MATCH (c:Cinema)
		WHERE NOT exists (c._deleted)
		RETURN c AS cinema
		""",
		mapOf()
	)

	/**
	 * Get all the Cinemas associated with a User.
	 * @param userId ID of the user whose favorite cinemas will be loaded.
	 */
	fun getFavoriteCinemas(userId: Long): Iterable<Cinema> = session.query<Cinema>(
		"""
		MATCH
			(u:User { id:{userID} })-[:GOESTO]->(c:Cinema)
		RETURN c AS cinema
		""",
		mapOf(
			"userID" to userId
		)
	)

	/**
	 * Get all the Cinemas which are active and whether it is favorited by the User.
	 * @param userId ID of the user whose favoritism will be taken into account.
	 */
	fun getCinemasAuth(userId: Long): Iterable<Cinema> = session.query<Cinema>(
		"""
			MATCH (c:Cinema)
			WHERE NOT exists (c._deleted)
			OPTIONAL MATCH (c)<-[f:GOESTO]-(u:User { id:{userID} })
			RETURN c AS cinema, f IS NOT NULL AS fav
		""",
		mapOf(
			"userID" to userId
		)
	)
}
