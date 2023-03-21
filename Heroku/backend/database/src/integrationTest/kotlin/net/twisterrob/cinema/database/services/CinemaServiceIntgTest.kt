package net.twisterrob.cinema.database.services

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.database.model.inUTC
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.ogm.session.Session

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
@TagIntegration
class CinemaServiceIntgTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: CinemaService

	@BeforeEach fun setUp(session: Session) {
		sut = CinemaService(session)
	}

	@Test fun `getActiveCinemas() ignores deleted cinemas and returns others`(session: Session) {
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 3)
		fixtCinemas[0]._deleted = null // active
		fixtCinemas[1]._deleted = fixture.build() // inactive
		fixtCinemas[2]._deleted = null // active
		fixtCinemas.forEach { it.inUTC() }
		fixtCinemas.forEach(session::save)
		session.clear()

		val result = sut.getActiveCinemas().toList()

		assertThat(result, containsInAnyOrder(sameBeanAs(fixtCinemas[0]), sameBeanAs(fixtCinemas[2])))
	}

	@Test fun `getFavoriteCinemas() returns the user's favorite cinemas, but not others`(session: Session) {
		val fixtCinemas: List<Cinema> = fixture.buildList()
		fixtCinemas.forEach { it.inUTC() }
		fixtCinemas.forEach(session::save)
		val fixtUser: User = fixture.build {
			cinemas = mutableListOf(fixtCinemas[0], fixtCinemas[2])
		}
		session.save(fixtUser)
		session.clear()

		val result = sut.getFavoriteCinemas(fixtUser.id).toList()

		assertThat(result, containsInAnyOrder(sameBeanAs(fixtCinemas[0]), sameBeanAs(fixtCinemas[2])))
	}

	@Test fun `getCinemasAuth() returns all cinemas, along with user's favorite info`(session: Session) {
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 4)
		fixtCinemas[0]._deleted = null // active
		fixtCinemas[1]._deleted = fixture.build() // inactive
		fixtCinemas[2]._deleted = fixture.build() // inactive
		fixtCinemas[3]._deleted = null // active
		fixtCinemas.forEach { it.inUTC() }
		fixtCinemas.forEach(session::save)
		val fixtUser: User = fixture.build {
			// User favorited one of the two active and an inactive cinema
			cinemas = mutableListOf(fixtCinemas[0], fixtCinemas[2])
		}
		session.save(fixtUser)
		session.clear()

		val result = sut.getCinemasAuth(fixtUser.id)

		assertThat(
			result,
			allOf(
				// Favorited and active cinema.
				hasEntry(sameBeanAs(fixtCinemas[0]), equalTo(true)),
				// Not favorited and not even active cinema.
				not(hasEntry(sameBeanAs(fixtCinemas[1]), anything())),
				// While favorited, it's not in the result, because it's inactive.
				not(hasEntry(sameBeanAs(fixtCinemas[2]), anything())),
				// Active Cinema, but not favorited.
				hasEntry(sameBeanAs(fixtCinemas[3]), equalTo(false))
			)
		)
		assertThat(result.entries, hasSize(2))
	}

	@Test fun `addFavorite() marks cinema as favorite`(session: Session) {
		val fixtCinemas: List<Cinema> = fixture.buildList()
		fixtCinemas.forEach { it.inUTC() }
		fixtCinemas.forEach(session::save)
		val fixtUser: User = fixture.build {
			inUTC()
			cinemas = mutableListOf(fixtCinemas[2])
		}
		session.save(fixtUser)
		session.clear()

		val result = sut.addFavorite(fixtUser.id, fixtCinemas[1].cineworldID)
		val expected = fixtCinemas[1].copy()
		expected.users = mutableListOf(fixtUser)
		fixtUser.cinemas = mutableSetOf(expected)
		assertThat(result, sameBeanAs(expected))
		session.clear()

		val favs = sut.getFavoriteCinemas(fixtUser.id).toList()
		assertThat(favs, containsInAnyOrder(sameBeanAs(result), sameBeanAs(fixtCinemas[2])))
	}

	@Test fun `removeFavorite() removes user's favorite cinema, but not others`(session: Session) {
		val fixtCinemas: List<Cinema> = fixture.buildList()
		fixtCinemas.forEach { it.inUTC() }
		fixtCinemas.forEach(session::save)
		val fixtUser: User = fixture.build {
			inUTC()
			cinemas = mutableListOf(fixtCinemas[0], fixtCinemas[2])
		}
		session.save(fixtUser)
		session.clear()

		val result = sut.removeFavorite(fixtUser.id, fixtCinemas[0].cineworldID)
		assertThat(result, sameBeanAs(fixtCinemas[0]))

		val favs = sut.getFavoriteCinemas(fixtUser.id).toList()
		assertThat(favs, containsInAnyOrder(sameBeanAs(fixtCinemas[2])))
	}

	@Test fun `removeFavorite() does not remove user's non-favorited cinema`(session: Session) {
		val fixtCinemas: List<Cinema> = fixture.buildList()
		fixtCinemas.forEach { it.inUTC() }
		fixtCinemas.forEach(session::save)
		val fixtUser: User = fixture.build {
			inUTC()
			cinemas = mutableListOf(fixtCinemas[0], fixtCinemas[2])
		}
		session.save(fixtUser)
		session.clear()

		val result = sut.removeFavorite(fixtUser.id, fixtCinemas[1].cineworldID)
		assertNull(result)

		val favs = sut.getFavoriteCinemas(fixtUser.id).toList()
		assertThat(favs, containsInAnyOrder(sameBeanAs(fixtCinemas[0]), sameBeanAs(fixtCinemas[2])))
	}
}
