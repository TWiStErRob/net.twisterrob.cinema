package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.User
import org.neo4j.ogm.session.Session
import javax.inject.Inject

class UserService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<User> =
		session.loadAll(User::class.java, 1)

	fun find(id: Long): User? =
		session.load(User::class.java, id, 2)

	fun delete(id: String) =
		session.delete(session.load(User::class.java, id))

	fun createOrUpdate(entity: User): User {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}
}

