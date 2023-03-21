package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserMapper
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaMapper
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmMapper
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import net.twisterrob.cinema.database.model.View as DBView

@ExtendWith(ModelFixtureExtension::class)
class ViewMapperUnitTest {

	private val mockFilmMapper: FilmMapper = mock()
	private val mockCinemaMapper: CinemaMapper = mock()
	private val mockUserMapper: UserMapper = mock()

	private lateinit var fixture: JFixture
	private lateinit var sut: ViewMapper

	@BeforeEach fun setUp() {
		sut = ViewMapper(mockCinemaMapper, mockFilmMapper, mockUserMapper)
		fixture.applyCustomisation {
			circularDependencyBehaviour().omitSpecimen() // View -> Film -> View
		}
	}

	@Test fun map() {
		fixture.customise().sameInstance(View::class.java, null) // prevent loop in val Film.view: View
		val fixtDBView: DBView = fixture.build()
		val fixtCinema: Cinema = fixture.build()
		val fixtFilm: Film = fixture.build()
		val fixtUser: User = fixture.build()
		whenever(mockCinemaMapper.map(fixtDBView.atCinema)).thenReturn(fixtCinema)
		whenever(mockFilmMapper.map(fixtDBView.watchedFilm, false)).thenReturn(fixtFilm)
		whenever(mockUserMapper.map(fixtDBView.userRef)).thenReturn(fixtUser)

		val view = sut.map(fixtDBView)

		assertThat(view, sameBeanAs(View(fixtDBView.date.toEpochMilli(), fixtFilm, fixtCinema, fixtUser)))

		verify(mockCinemaMapper).map(fixtDBView.atCinema)
		verify(mockFilmMapper).map(fixtDBView.watchedFilm, false)
		verify(mockUserMapper).map(fixtDBView.userRef)
		verifyNoMoreInteractions(mockCinemaMapper, mockFilmMapper, mockUserMapper)
	}
}
