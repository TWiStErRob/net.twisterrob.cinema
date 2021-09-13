package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.User
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.query
import org.neo4j.ogm.session.queryForObject
import javax.inject.Inject

class FilmService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Film> =
		session.loadAll(Film::class.java, 0)

	fun find(id: String): Film? =
		session.load(Film::class.java, id, 1)

	fun delete(id: String) {
		session.delete(session.load(Film::class.java, id))
	}

	fun createOrUpdate(entity: Film): Film {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	fun save(list: List<Film>) {
		session.save(list)
	}

	/**
	 * Return all Films which are active.
	 */
	fun getActiveFilms(): Iterable<Film> =
		session.query<Film>(
			"""
			MATCH (f:Film)
			WHERE NOT exists(f._deleted)
			RETURN f AS film
			""",
			mapOf()
		)

	/**
	 * Find a film by [edi].
	 * @param edi [Film.edi]
	 */
	fun getFilm(edi: Long): Film? = session.queryForObject(
		"""
		MATCH (f:Film { edi: ${"$"}filmEDI })
		RETURN f AS film
		""",
		mapOf(
			"filmEDI" to edi
		)
	)

	/**
	 * Find a list of films by edis.
	 * @param filmEDIs films to return (array of [Film.edi])
	 */
	fun getFilms(filmEDIs: List<Long>): Iterable<Film> =
		session.query<Film>(
			"""
			MATCH (f:Film)
			WHERE NOT exists(f._deleted) AND f.edi IN ${"$"}filmEDIs
			RETURN f AS film
			""",
			mapOf(
				"filmEDIs" to filmEDIs
			)
		)

	/**
	 * Find a list of films by edis.
	 * @param filmEDIs films to return (array of [Film.edi])
	 * @param userID [User.id]
	 */
	fun getFilmsAuth(filmEDIs: List<Long>, userID: String): Iterable<Film> =
		session.query<Film>(
			"""
			MATCH (f:Film)
			WHERE //not exists(f._deleted) and
			f.edi IN ${"$"}filmEDIs
			OPTIONAL MATCH
				(f)<-[w:WATCHED]-(v:View),
				(v)<-[a:ATTENDED]-(u:User { id: ${"$"}userID }),
				(v)-[at:AT]->(c:Cinema)
			RETURN
				f AS film,
				w AS watched, v AS view,
				a AS attended, u AS user,
				at AS at, c AS cinema
			""",
			mapOf(
				"userID" to userID,
				"filmEDIs" to filmEDIs
			)
		)
}
