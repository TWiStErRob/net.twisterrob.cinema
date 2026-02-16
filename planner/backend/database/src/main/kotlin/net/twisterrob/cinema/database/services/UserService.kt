package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.User
import net.twisterrob.neo4j.ogm.TimestampConverter
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.load
import java.time.OffsetDateTime
import javax.inject.Inject

class UserService @Inject constructor(
	private val session: Session
) {

	fun find(id: String): User? =
		session.load(id, depth = 0)

	/**
	 * Create a user if it doesn't exist, with the specified data.
	 * @param userId OpenID
	 * @param email The email of the user being created.
	 * @param name Display name of the user being created.
	 * @param realm The website where the login happened.
	 * @param created Moment of creation.
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
			""".trimIndent(),
			mapOf(
				"realm" to realm,
				"id" to userId,
				"email" to email,
				"name" to name,
				// calling the converter is workaround for https://github.com/neo4j/neo4j-ogm/issues/766
				"created" to (TimestampConverter().toGraphProperty(created) ?: error("$created produced null"))
			)
		) ?: error("Cannot find user ${userId} right after creating it.")
}
