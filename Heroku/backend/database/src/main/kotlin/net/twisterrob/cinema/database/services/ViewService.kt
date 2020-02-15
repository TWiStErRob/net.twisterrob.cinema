package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.View
import org.neo4j.ogm.session.Session
import javax.inject.Inject

class ViewService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<View> =
		session.loadAll(View::class.java, 2)

	fun find(id: String): View? =
		session.load(View::class.java, id, 1)

	fun delete(id: String) =
		session.delete(session.load(View::class.java, id))

	fun createOrUpdate(entity: View): View {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}
}
