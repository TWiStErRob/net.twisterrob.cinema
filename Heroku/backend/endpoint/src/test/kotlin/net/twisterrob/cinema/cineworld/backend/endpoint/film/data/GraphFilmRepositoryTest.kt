package net.twisterrob.cinema.cineworld.backend.endpoint.film.data

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.cinema.database.services.FilmService
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.offsetDateTimeRealistic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film as FrontendFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

class GraphFilmRepositoryTest {

	@Mock lateinit var mockService: FilmService
	@Mock lateinit var mockMapper: FilmMapper

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
		add(offsetDateTimeRealistic())
	}
	private lateinit var sut: FilmRepository

	@BeforeEach fun setUp() {
		MockitoAnnotations.initMocks(this)
		sut = GraphFilmRepository(mockService, mockMapper)
	}

	@Test fun `get film by edi`() {
		val fixtDBFilm: DBFilm = fixture.build()
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
