package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.User
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import org.neo4j.ogm.session.query
import org.neo4j.ogm.session.queryForObject
import java.time.LocalDate
import javax.inject.Inject

class FilmService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Film> =
		session.loadAll(depth = 0)

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
			WHERE f._deleted IS NULL
			RETURN f AS film
			""",
			mapOf()
		)

	/**
	 * Find a film by [edi].
	 * @param edi [Film.edi]
	 */
	fun getFilm(edi: Long): Film? =
		session.queryForObject(
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
			WHERE
				f._deleted IS NULL
				AND f.edi IN ${"$"}filmEDIs
			RETURN f AS film
			""",
			mapOf(
				"filmEDIs" to filmEDIs
			)
		)

	/**
	 * Find a list of films by date in specific cinemas.
	 * @param date on which day are the screenings of the film?
	 * @param cinemaIDs which cinemas are screening the film? (array of [Cinema.cineworldID]s)
	 */
	fun getFilms(date: LocalDate, cinemaIDs: List<Long>): Iterable<Film> =
		session.query<Film>(
			"""
			MATCH (f:Film) WHERE f._deleted IS NULL
			MATCH (c:Cinema) WHERE c.cineworldID IN ${"$"}cinemaIDs
			MATCH (p:Performance) WHERE date.truncate('day', p.time) = ${"$"}date
			MATCH
				(p)-[in:IN]->(c),
				(p)-[s:SCREENS]->(f)
			RETURN DISTINCT f AS film
			""",
			mapOf(
				"date" to date,
				"cinemaIDs" to cinemaIDs,
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
			WHERE
				f._deleted IS NULL
				AND f.edi IN ${"$"}filmEDIs
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

	/**
	 * Find a list of films by date in specific cinemas.
	 * @param date on which day are the screenings of the film?
	 * @param cinemaIDs which cinemas are screening the film? (array of [Cinema.cineworldID]s)
	 * @param userID [User.id]
	 */
	fun getFilmsAuth(date: LocalDate, cinemaIDs: List<Long>, userID: String): Iterable<Film> =
		session.query<Film>(
			"""
			MATCH (f:Film) WHERE f._deleted IS NULL
			MATCH (c:Cinema) WHERE c.cineworldID IN ${"$"}cinemaIDs
			MATCH (p:Performance) WHERE date.truncate('day', p.time) = ${"$"}date
			MATCH
				(p)-[in:IN]->(c),
				(p)-[s:SCREENS]->(f)
			OPTIONAL MATCH
				(f)<-[w:WATCHED]-(v:View),
				(v)<-[a:ATTENDED]-(u:User { id: ${"$"}userID }),
				(v)-[at:AT]->(c:Cinema)
			RETURN
				f AS film,
				s AS screen, p AS performance,
				w AS watched, v AS view,
				a AS attended, u AS user,
				in AS inCinema, at AS atCinema, c AS cinema
			""",
			mapOf(
				"date" to date,
				"cinemaIDs" to cinemaIDs,
				"userID" to userID,
			)
		)
}
