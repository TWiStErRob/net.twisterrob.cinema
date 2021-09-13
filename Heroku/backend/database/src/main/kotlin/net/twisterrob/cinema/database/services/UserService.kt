package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.User
import net.twisterrob.neo4j.ogm.TimestampConverter
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.load
import org.neo4j.ogm.session.queryForObject
import java.time.OffsetDateTime
import javax.inject.Inject

class UserService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<User> =
		session.loadAll(User::class.java, 1)

	fun find(id: String): User? =
		session.load(id, 0)

	fun delete(id: String) {
		session.delete(session.load(User::class.java, id))
	}

	fun createOrUpdate(entity: User): User {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	/**
	 * Create a user if doesn't exists with the specified data.
	 * @param userId OpenID
	 * @param name Display name of the user being created.
	 * @param email The email of the user being created.
	 * @param created Moment of creation.
	 * @param realm The website where the login happened.
	 */
	fun addUser(userId: String, email: String, name: String, realm: String, created: OffsetDateTime): User =
		session.queryForObject(
			"""
			MERGE (u:User { id: ${"$"}id })
			ON CREATE SET u._created = ${"$"}created
			ON CREATE SET u.class = "User"

			ON CREATE SET u.email = ${"$"}email
			ON CREATE SET u.name = ${"$"}name
			ON CREATE SET u.realm = ${"$"}realm

			ON MATCH SET u.email = ${"$"}email
			ON MATCH SET u.name = ${"$"}name
			ON MATCH SET u.realm = ${"$"}realm

			RETURN u AS user
			""",
			mapOf(
				"realm" to realm,
				"id" to userId,
				"email" to email,
				"name" to name,
				// calling the converter is workaround for https://github.com/neo4j/neo4j-ogm/issues/766
				"created" to TimestampConverter().toGraphProperty(created)!!
			)
		)!!
}
