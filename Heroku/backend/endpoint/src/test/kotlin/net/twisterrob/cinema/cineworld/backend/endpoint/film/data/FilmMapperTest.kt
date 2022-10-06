package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewMapper
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

@ExtendWith(ModelFixtureExtension::class)
class FilmMapperTest {

	private val mockViewMapper: ViewMapper = mock()

	private lateinit var fixture: JFixture
	private lateinit var sut: FilmMapper

	@BeforeEach fun setUp() {
		sut = FilmMapper { mockViewMapper }
		fixture.applyCustomisation {
			circularDependencyBehaviour().omitSpecimen() // View -> Film -> View
		}
	}

	@Test fun `map a film (defaults to with views)`() {
		val fixtDBFilm: DBFilm = fixture.build<DBFilm>().apply {
			views = fixture.buildList()
		}

		val fixtView: View = fixture.build()
		whenever(mockViewMapper.map(any())).thenReturn(fixtView)

		val film = sut.map(fixtDBFilm)

		assertProperties(fixtDBFilm, film)
		assertThat(film.view, equalTo(fixtView))

		verify(mockViewMapper).map(fixtDBFilm.views.elementAt(0))
		verifyNoMoreInteractions(mockViewMapper)
	}

	@Test fun `map a film with views`() {
		val fixtDBFilm: DBFilm = fixture.build<DBFilm>().apply {
			views = fixture.buildList()
		}
		val fixtView: View = fixture.build()
		whenever(mockViewMapper.map(any())).thenReturn(fixtView)

		val film = sut.map(fixtDBFilm, mapViews = true)

		assertProperties(fixtDBFilm, film)
		assertThat(film.view, equalTo(fixtView))

		verify(mockViewMapper).map(fixtDBFilm.views.elementAt(0))
		verifyNoMoreInteractions(mockViewMapper)
	}

	@Test fun `map a film with views, but has no views`() {
		val fixtDBFilm: DBFilm = fixture.build<DBFilm>().apply {
			views = fixture.buildList(size = 0)
		}

		val film = sut.map(fixtDBFilm, mapViews = true)

		assertProperties(fixtDBFilm, film)
		assertThat(film.view, nullValue())

		verifyNoInteractions(mockViewMapper)
	}

	@Test fun `map a film without views`() {
		val fixtDBFilm: DBFilm = fixture.build()

		val film = sut.map(fixtDBFilm, mapViews = false)

		assertProperties(fixtDBFilm, film)
		assertThat(film.view, nullValue())

		verifyNoInteractions(mockViewMapper)
	}

	private fun assertProperties(expected: DBFilm, actual: FrontendFilm) {
		assertThat(actual.cineworldID, equalTo(expected.cineworldID))
		assertThat(actual.title, equalTo(expected.title))
		assertThat(actual._created, equalTo(expected._created))
		assertThat(actual._updated, equalTo(expected._updated))
		assertThat(actual.cineworldID, equalTo(expected.cineworldID))
		assertThat(actual.title, equalTo(expected.title))
		assertThat(actual.director, equalTo(expected.director))
		assertThat(actual.release, equalTo(expected.release))
		assertThat(actual.format, equalTo(expected.format))
		assertThat(actual.runtime, equalTo(expected.runtime))
		assertThat(actual.poster_url, equalTo(expected.poster_url))
		assertThat(actual.cineworldInternalID, equalTo(expected.cineworldInternalID))
		assertThat(actual.cert, equalTo(expected.cert))
		assertThat(actual.imax, equalTo(expected.imax))
		assertThat(actual.`3D`, equalTo(expected.`3D`))
		assertThat(actual.film_url, equalTo(expected.film_url))
		assertThat(actual.edi, equalTo(expected.edi))
		assertThat(actual.classification, equalTo(expected.classification))
		assertThat(actual.trailer, equalTo(expected.trailer))
		assertThat(actual.actors, equalTo(expected.actors))
		assertThat(actual.originalTitle, equalTo(expected.originalTitle))
		assertThat(actual.categories, equalTo(expected.categories))
		assertThat(actual.weighted, equalTo(expected.weighted))
		assertThat(actual.slug, equalTo(expected.slug))
		assertThat(actual.group, equalTo(expected.group))
		assertThat(actual._created, equalTo(expected._created))
		assertThat(actual._updated, equalTo(expected._updated))
		assertThat(actual.`class`, equalTo(expected.className))
	}
}
