package net.twisterrob.cinema.database.services

import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.database.model.View
import org.neo4j.ogm.session.Session
import javax.inject.Inject

interface CinemaServices {

	val cinemaService: CinemaService
	val filmService: FilmService
	val viewService: ViewService
	val userService: UserService
}

class CinemaService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Cinema> = session.loadAll(Cinema::class.java, 0)

	fun find(id: String): Cinema? = session.load(Cinema::class.java, id, 1)

	fun delete(id: String) = delete(session.load(Cinema::class.java, id))
	fun delete(entity: Cinema) = session.delete(entity)

	fun createOrUpdate(entity: Cinema): Cinema {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	fun save(list: List<Cinema>) = session.save(list)
}

class FilmService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<Film> = session.loadAll(Film::class.java, 0)

	fun find(id: String): Film? = session.load(Film::class.java, id, 1)

	fun delete(id: String) = session.delete(session.load(Film::class.java, id))

	fun createOrUpdate(entity: Film): Film {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}

	fun save(list: List<Film>) = session.save(list)
}

class ViewService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<View> = session.loadAll(View::class.java, 2)

	fun find(id: String): View? = session.load(View::class.java, id, 1)

	fun delete(id: String) = session.delete(session.load(View::class.java, id))

	fun createOrUpdate(entity: View): View {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}
}

class UserService @Inject constructor(
	private val session: Session
) {

	fun findAll(): Iterable<User> = session.loadAll(User::class.java, 1)

	fun find(id: Long): User? = session.load(User::class.java, id, 2)

	fun delete(id: String) = session.delete(session.load(User::class.java, id))

	fun createOrUpdate(entity: User): User {
		session.save(entity, 10)
//		return session.load(Ad::class.java, entity.adId, 1)
		return entity
	}
}
