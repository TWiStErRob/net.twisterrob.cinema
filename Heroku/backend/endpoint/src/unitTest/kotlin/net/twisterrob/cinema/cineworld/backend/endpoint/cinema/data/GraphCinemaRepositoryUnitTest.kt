package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.buildMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema as FrontendCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

@ExtendWith(ModelFixtureExtension::class)
@ExtendWith(MockitoExtension::class)
class GraphCinemaRepositoryUnitTest {

	@Mock lateinit var mockService: CinemaService
	@Mock lateinit var mockMapper: CinemaMapper

	private lateinit var fixture: JFixture
	private lateinit var sut: CinemaRepository

	@BeforeEach fun setUp() {
		sut = GraphCinemaRepository(mockService, mockMapper)
	}

	@Test fun `list all cinemas`() {
		val fixtDBCinemas: List<DBCinema> = fixture.buildList()
		val fixtCinemas: List<FrontendCinema> = fixture.buildList()
		fixtDBCinemas.zip(fixtCinemas).forEach { (db, frontend) ->
			whenever(mockMapper.map(db)).thenReturn(frontend)
		}
		whenever(mockService.getActiveCinemas()).thenReturn(fixtDBCinemas)

		val cinemas = sut.getActiveCinemas()

		assertEquals(fixtCinemas, cinemas)

		verify(mockService).getActiveCinemas()
		fixtDBCinemas.forEach { verify(mockMapper).map(it) }
		verifyNoMoreInteractions(mockService, mockMapper)
	}

	@Test fun `list favorite cinemas`() {
		val fixtDBCinemas: List<DBCinema> = fixture.buildList()
		val fixtCinemas: List<FrontendCinema> = fixture.buildList()
		val fixtUser: String = fixture.build()
		fixtDBCinemas.zip(fixtCinemas).forEach { (db, frontend) ->
			whenever(mockMapper.map(db)).thenReturn(frontend)
		}
		whenever(mockService.getFavoriteCinemas(fixtUser)).thenReturn(fixtDBCinemas)

		val cinemas = sut.getFavoriteCinemas(fixtUser)

		assertEquals(fixtCinemas, cinemas)

		verify(mockService).getFavoriteCinemas(fixtUser)
		fixtDBCinemas.forEach { verify(mockMapper).map(it) }
		verifyNoMoreInteractions(mockService, mockMapper)
	}

	@Test fun `list all cinemas with user favorites`() {
		val fixtDBCinemas: Map<DBCinema, Boolean> = fixture.buildMap()
		val fixtCinemas: List<FrontendCinema> = fixture.buildList()
		val fixtUser: String = fixture.build()
		fixtDBCinemas.entries.zip(fixtCinemas).forEach { (db, frontend) ->
			whenever(mockMapper.map(db)).thenReturn(frontend)
		}
		whenever(mockService.getCinemasAuth(fixtUser)).thenReturn(fixtDBCinemas)

		val cinemas = sut.getCinemasAuth(fixtUser)

		assertEquals(fixtCinemas, cinemas)

		verify(mockService).getCinemasAuth(fixtUser)
		fixtDBCinemas.forEach { verify(mockMapper).map(it) }
		verifyNoMoreInteractions(mockService, mockMapper)
	}

	@Test fun `add cinema as favorite for user`() {
		val fixtDBCinema: DBCinema = fixture.build()
		val fixtCinema: FrontendCinema = fixture.build()
		val fixtUser: String = fixture.build()
		val fixtCinemaId: Long = fixture.build()
		whenever(mockMapper.map(fixtDBCinema)).thenReturn(fixtCinema)
		whenever(mockService.addFavorite(fixtUser, fixtCinemaId)).thenReturn(fixtDBCinema)

		val cinema = sut.addFavorite(fixtUser, fixtCinemaId)

		assertEquals(fixtCinema, cinema)

		verify(mockService).addFavorite(fixtUser, fixtCinemaId)
		verify(mockMapper).map(fixtDBCinema)
		verifyNoMoreInteractions(mockService, mockMapper)
	}

	@Test fun `add cinema as favorite for non-existent cinema`() {
		val fixtUser: String = fixture.build()
		val fixtCinemaId: Long = fixture.build()
		whenever(mockService.addFavorite(fixtUser, fixtCinemaId)).thenReturn(null)

		val cinema = sut.addFavorite(fixtUser, fixtCinemaId)

		assertNull(cinema)

		verify(mockService).addFavorite(fixtUser, fixtCinemaId)
		verifyNoMoreInteractions(mockService, mockMapper)
	}

	@Test fun `remove cinema as favorite for user`() {
		val fixtDBCinema: DBCinema = fixture.build()
		val fixtCinema: FrontendCinema = fixture.build()
		val fixtUser: String = fixture.build()
		val fixtCinemaId: Long = fixture.build()
		whenever(mockMapper.map(fixtDBCinema)).thenReturn(fixtCinema)
		whenever(mockService.removeFavorite(fixtUser, fixtCinemaId)).thenReturn(fixtDBCinema)

		val cinema = sut.removeFavorite(fixtUser, fixtCinemaId)

		assertEquals(fixtCinema, cinema)

		verify(mockService).removeFavorite(fixtUser, fixtCinemaId)
		verify(mockMapper).map(fixtDBCinema)
		verifyNoMoreInteractions(mockService, mockMapper)
	}

	@Test fun `remove cinema as favorite for non-existent cinema`() {
		val fixtUser: String = fixture.build()
		val fixtCinemaId: Long = fixture.build()
		whenever(mockService.removeFavorite(fixtUser, fixtCinemaId)).thenReturn(null)

		val cinema = sut.removeFavorite(fixtUser, fixtCinemaId)

		assertNull(cinema)

		verify(mockService).removeFavorite(fixtUser, fixtCinemaId)
		verifyNoMoreInteractions(mockService, mockMapper)
	}
}
