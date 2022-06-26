package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import net.twisterrob.cinema.cineworld.quickbook.QuickbookService
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.services.FilmService
import net.twisterrob.test.build
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

@ExtendWith(ModelFixtureExtension::class)
@ExtendWith(MockitoExtension::class)
class QuickbookFilmRepositoryTest {

	@Mock lateinit var mockService: FilmService
	@Mock lateinit var mockMapper: FilmMapper
	@Mock lateinit var mockQuickbook: QuickbookService

	private lateinit var fixture: JFixture
	private lateinit var sut: FilmRepository

	@BeforeEach fun setUp() {
		sut = QuickbookFilmRepository(mockService, mockMapper, mockQuickbook)
	}

	@Test fun `get film by edi`() {
		val fixtDBFilm: DBFilm = fixture.build()
		fixture.customise().sameInstance(View::class.java, null) // prevent loop in val Film.view: View
		val fixtFilm: FrontendFilm = fixture.build()
		val fixtFilmId: Long = fixture.build()
		whenever(mockMapper.map(fixtDBFilm)).thenReturn(fixtFilm)
		whenever(mockService.getFilm(fixtFilmId)).thenReturn(fixtDBFilm)

		val film = sut.getFilm(fixtFilmId)

		assertEquals(fixtFilm, film)

		verify(mockService).getFilm(fixtFilmId)
		verify(mockMapper).map(fixtDBFilm)
		verifyNoMoreInteractions(mockService, mockMapper)
	}
}
