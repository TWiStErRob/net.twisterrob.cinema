package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Film
import net.twisterrob.neo4j.ogm.queryForObject
import org.neo4j.ogm.session.Session
import javax.inject.Inject

class FilmService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Film> =
		session.loadAll(Film::class.java, 0)

	fun find(id: String): Film? =
		session.load(Film::class.java, id, 1)

	fun delete(id: String) =
		session.delete(session.load(Film::class.java, id))

	fun createOrUpdate(entity: Film): Film {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	fun save(list: List<Film>) =
		session.save(list)

	/**
	 * Find a film by [edi].
	 * @param edi [Film.edi]
	 */
	fun getFilm(edi: Long): Film? = session.queryForObject(
		"""
		MATCH (f:Film {edi: {filmEDI}})
		RETURN f AS film
		""",
		mapOf(
			"filmEDI" to edi
		)
	)
}
