package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.shared.getTypedValue
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import org.neo4j.ogm.session.query
import javax.inject.Inject

@Suppress("StringLiteralDuplication")
class CinemaService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Cinema> =
		session.loadAll(depth = 0)

	fun save(list: List<Cinema>) {
		session.save(list)
	}

	/**
	 * Get all the Cinemas which are active.
	 */
	fun getActiveCinemas(): Iterable<Cinema> =
		session.query(
			"""
				MATCH (c:Cinema)
				WHERE c._deleted IS NULL
				RETURN c AS cinema
			""".trimIndent()
		)

	/**
	 * Get all the Cinemas associated with a User.
	 * @param userId ID of the user whose favorite cinemas will be loaded.
	 */
	fun getFavoriteCinemas(userId: String): Iterable<Cinema> =
		session.query<Cinema>(
			"""
				MATCH (u:User { id: ${"$"}userID })-[:GOESTO]->(c:Cinema)
				RETURN c AS cinema
			""".trimIndent(),
			mapOf(
				"userID" to userId
			)
		)

	/**
	 * Get all the Cinemas which are active and whether it is favorited by the User.
	 * @param userId ID of the user whose favoritism will be taken into account.
	 */
	fun getCinemasAuth(userId: String): Map<Cinema, Boolean> =
		session
			.query(
				"""
					MATCH (c:Cinema)
					WHERE c._deleted IS NULL
					OPTIONAL MATCH (c)<-[f:GOESTO]-(u:User { id: ${"$"}userID })
					RETURN c AS cinema, f IS NOT NULL AS fav
				""".trimIndent(),
				mapOf(
					"userID" to userId
				)
			)
			.queryResults()
			.associateBy(
				keySelector = { it.getTypedValue("cinema") },
				valueTransform = { it.getTypedValue("fav") }
			)

	/**
	 * Create a `GOESTO` relation between [User] and [Cinema] if it doesn't exist.
	 * @param userId [User.id]
	 * @param cinema [Cinema.cineworldID]
	 * @return updated [Cinema], or `null` if [cinema] is not a known [Cinema].
	 */
	fun addFavorite(userId: String, cinema: Long): Cinema? =
		session.queryForObject(
			"""
				MATCH (c:Cinema { cineworldID: ${"$"}cinemaID })
				MATCH (u:User { id: ${"$"}userID })
				MERGE (u)-[g:GOESTO]->(c)
				RETURN u AS user, c AS cinema, g as favorite
			""".trimIndent(),
			mapOf(
				"userID" to userId,
				"cinemaID" to cinema
			)
		)

	/**
	 * Find the [User] and remove the relation `GOESTO` to the [Cinema].
	 * @param userId [User.id]
	 * @param cinema [Cinema.cineworldID]
	 * @return updated [Cinema], or `null` if [cinema] is not a known [Cinema].
	 */
	fun removeFavorite(userId: String, cinema: Long): Cinema? =
		session.queryForObject(
			"""
				MATCH (u:User { id: ${"$"}userID })-[g:GOESTO]->(c:Cinema { cineworldID: ${"$"}cinemaID })
				DELETE g
				RETURN u AS user, c AS cinema
			""".trimIndent(),
			mapOf(
				"userID" to userId,
				"cinemaID" to cinema
			)
		)
}
