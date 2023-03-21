package net.twisterrob.cinema.database.services

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.Film
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.model.View
import net.twisterrob.cinema.database.model.fixup
import net.twisterrob.cinema.database.model.inUTC
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.ogm.session.Session

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
class FilmServiceIntgTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: FilmService

	@BeforeEach fun setUp(session: Session) {
		sut = FilmService(session)
	}

	@Test fun `getActiveFilms() ignores deleted films and returns others`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 3)
		fixtFilms[0]._deleted = null // active
		fixtFilms[1]._deleted = fixture.build() // inactive
		fixtFilms[2]._deleted = null // active
		fixtFilms.forEach { it.inUTC() }
		fixtFilms.forEach(session::save)
		session.clear()

		val result = sut.getActiveFilms().toList()

		assertThat(result, containsInAnyOrder(sameBeanAs(fixtFilms[0]), sameBeanAs(fixtFilms[2])))
	}

	@Test fun `getAllFilms() includes deleted films`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 3)
		fixtFilms[0]._deleted = null // active
		fixtFilms[1]._deleted = fixture.build() // inactive
		fixtFilms[2]._deleted = null // active
		fixtFilms.forEach { it.inUTC() }
		fixtFilms.forEach(session::save)
		session.clear()

		val result = sut.getAllFilms().toList()

		assertThat(
			result,
			containsInAnyOrder(sameBeanAs(fixtFilms[0]), sameBeanAs(fixtFilms[1]), sameBeanAs(fixtFilms[2]))
		)
	}

	@Test fun `getFilm() by edi returns null when not found`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 3)
		fixtFilms.forEach(session::save)
		session.clear()

		val result = sut.getFilm(edi = fixture.build())

		assertNull(result)
	}

	@Test fun `getFilm() by edi returns right film`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 3)
		fixtFilms.forEach { it.inUTC() }
		fixtFilms.forEach(session::save)
		session.clear()

		val result = sut.getFilm(edi = fixtFilms[1].edi)

		assertNotNull(result)
		assertThat(result, sameBeanAs(fixtFilms[1]))
	}

	@Test fun `getFilms() by edi returns empty list when not found`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 3)
		fixtFilms.forEach(session::save)
		session.clear()

		val result = sut.getFilms(filmEDIs = fixture.build()).toList()

		assertThat(result, empty())
	}

	@Test fun `getFilms() by edi returns right films`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 5)
		fixtFilms.forEach { it.inUTC() }
		fixtFilms.forEach { it._deleted = null }
		fixtFilms[2]._deleted = fixture.build()
		fixtFilms[3]._deleted = fixture.build()
		fixtFilms.forEach(session::save)
		session.clear()

		// [2] is deleted, so it gets filtered out.
		val filmEDIs = listOf(fixtFilms[0].edi, fixtFilms[2].edi, fixtFilms[4].edi)
		val result = sut.getFilms(filmEDIs = filmEDIs).toList()

		assertThat(result, sameBeanAs(listOf(fixtFilms[0], fixtFilms[4])))
	}

	@Test fun `getFilmsAuth() by edi returns empty list for empty database`() {
		val result = sut.getFilmsAuth(filmEDIs = fixture.build(), userID = fixture.build()).toList()

		assertThat(result, empty())
	}

	@Test fun `getFilmsAuth() by edi returns films with no views when invalid user`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 5)
		fixtFilms.forEach { it._deleted = null }
		fixtFilms[2]._deleted = fixture.build()
		fixtFilms[3]._deleted = fixture.build()
		fixtFilms.forEach { it.inUTC() }
		fixtFilms.forEach(session::save)
		session.clear()

		// [2] is deleted, so it gets filtered out.
		val filmEDIs = listOf(fixtFilms[0].edi, fixtFilms[2].edi, fixtFilms[4].edi)
		val result = sut.getFilmsAuth(filmEDIs = filmEDIs, userID = fixture.build()).toList()

		result.forEach { assertThat(it.views, empty()) }
		assertThat(result, sameBeanAs(listOf(fixtFilms[0], fixtFilms[4])))
	}

	@Test fun `getFilmsAuth() by edi returns list when valid user with a view`(session: Session) {
		val fixtFilms: List<Film> = fixture.buildList(size = 5)
		fixtFilms.forEach { it._deleted = null }
		fixtFilms[2]._deleted = fixture.build()
		fixtFilms[3]._deleted = fixture.build()
		fixtFilms.forEach { it.inUTC() }
		val fixtView: View = fixture.build {
			atCinema = fixture.build()
			watchedFilm = fixtFilms[4]
			userRef = fixture.build()
			inUTC()
		}
		fixup(fixtView, *fixtFilms.toTypedArray())

		fixtFilms.forEach(session::save)
		session.save(fixtView)
		session.clear()

		// [2] is deleted, so it gets filtered out.
		val filmEDIs = listOf(fixtFilms[0].edi, fixtFilms[2].edi, fixtFilms[4].edi)
		val result = sut.getFilmsAuth(filmEDIs = filmEDIs, userID = fixtView.userRef.id).toList()

		assertThat(result[0].views, empty())
		assertThat(result.sortedBy { it.edi }, sameBeanAs(listOf(fixtFilms[0], fixtFilms[4]).sortedBy { it.edi }))
	}

	// TODO cover positive cases.
	@Test fun `getFilms() by date returns empty list for empty database`() {
		val result = sut.getFilms(date = fixture.build(), cinemaIDs = fixture.build()).toList()

		assertThat(result, empty())
	}

	// TODO cover positive cases.
	@Test fun `getFilmsAuth() by date returns empty list for empty database`() {
		val result =
			sut.getFilmsAuth(date = fixture.build(), cinemaIDs = fixture.build(), userID = fixture.build()).toList()

		assertThat(result, empty())
	}
}
