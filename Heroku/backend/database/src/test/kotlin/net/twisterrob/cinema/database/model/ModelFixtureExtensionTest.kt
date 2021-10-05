package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ModelFixtureExtension::class)
class ModelFixtureExtensionTest {

	private lateinit var fixture: JFixture

	@Test fun `JFixture injection works`() {
		assertTrue(::fixture.isInitialized, "JFixture injection is broken")
	}

	@Test fun `Cinema fixtures have different fields`() {
		val fixtCinema1: Cinema = fixture.build()
		val fixtCinema2: Cinema = fixture.build()

		assertAll {
			o { assertEquals("Cinema", fixtCinema1.className) }
			o { assertEquals("Cinema", fixtCinema2.className) }
			o { assertNull(fixtCinema1.graphId) }
			o { assertNull(fixtCinema2.graphId) }
			o { assertNotEquals(fixtCinema1._created, fixtCinema2._created) }
			o { assertNotEquals(fixtCinema1._updated, fixtCinema2._updated) }
			o { assertNotEquals(fixtCinema1._deleted, fixtCinema2._deleted) }
			o { assertNotEquals(fixtCinema1.cineworldID, fixtCinema2.cineworldID) }
			o { assertNotEquals(fixtCinema1.name, fixtCinema2.name) }
			o { assertNotEquals(fixtCinema1.postcode, fixtCinema2.postcode) }
			o { assertNotEquals(fixtCinema1.address, fixtCinema2.address) }
			o { assertNotEquals(fixtCinema1.telephone, fixtCinema2.telephone) }
			o { assertNotEquals(fixtCinema1.cinema_url, fixtCinema2.cinema_url) }
			o { assertThat(fixtCinema1.users, empty()) }
			o { assertThat(fixtCinema2.users, empty()) }
			o { assertThat(fixtCinema1.views, empty()) }
			o { assertThat(fixtCinema2.views, empty()) }
		}
	}

	@Test fun `Film fixtures have different fields`() {
		val fixtFilm1: Film = fixture.build()
		val fixtFilm2: Film = fixture.build()

		assertAll {
			o { assertEquals("Film", fixtFilm1.className) }
			o { assertEquals("Film", fixtFilm2.className) }
			o { assertNull(fixtFilm1.graphId) }
			o { assertNull(fixtFilm2.graphId) }
			o { assertNotEquals(fixtFilm1._created, fixtFilm2._created) }
			o { assertNotEquals(fixtFilm1._updated, fixtFilm2._updated) }
			o { assertNotEquals(fixtFilm1._deleted, fixtFilm2._deleted) }
			o { assertNotEquals(fixtFilm1.edi, fixtFilm2.edi) }
			o { assertNotEquals(fixtFilm1.cineworldID, fixtFilm2.cineworldID) }
			o { assertNotEquals(fixtFilm1.cineworldInternalID, fixtFilm2.cineworldInternalID) }
			o { assertNotEquals(fixtFilm1.title, fixtFilm2.title) }
			o { assertNotEquals(fixtFilm1.originalTitle, fixtFilm2.originalTitle) }
			o { assertNotEquals(fixtFilm1.advisory, fixtFilm2.advisory) }
			o { assertNotEquals(fixtFilm1.classification, fixtFilm2.classification) }
			o { assertNotEquals(fixtFilm1.cert, fixtFilm2.cert) }
			o { assertNotEquals(fixtFilm1.actors, fixtFilm2.actors) }
			o { assertNotEquals(fixtFilm1.director, fixtFilm2.director) }
			//o { assertNotEquals(fixtFilm1.imax, fixtFilm2.imax) } // Not possible for Boolean
			//o { assertNotEquals(fixtFilm1.`3D`, fixtFilm2.`3D`) } // Not possible for Boolean
			o { assertNotEquals(fixtFilm1.runtime, fixtFilm2.runtime) }
			o { assertNotEquals(fixtFilm1.weighted, fixtFilm2.weighted) }
			o { assertNotEquals(fixtFilm1.slug, fixtFilm2.slug) }
			o { assertNotEquals(fixtFilm1.group, fixtFilm2.group) }
			o { assertNotEquals(fixtFilm1.format, fixtFilm2.format) }
			o { assertNotEquals(fixtFilm1.still_url, fixtFilm2.still_url) }
			o { assertNotEquals(fixtFilm1.film_url, fixtFilm2.film_url) }
			o { assertNotEquals(fixtFilm1.poster_url, fixtFilm2.poster_url) }
			o { assertNotEquals(fixtFilm1.poster, fixtFilm2.poster) }
			o { assertNotEquals(fixtFilm1.trailer, fixtFilm2.trailer) }
			o { assertNotEquals(fixtFilm1.release, fixtFilm2.release) }
			o { assertNotEquals(fixtFilm1.categories, fixtFilm2.categories) }
			o { assertThat(fixtFilm1.views, empty()) }
			o { assertThat(fixtFilm2.views, empty()) }
		}
	}
}
