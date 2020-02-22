package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.buildMap
import net.twisterrob.test.offsetDateTimeRealistic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNull
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema as FrontendCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

class GraphCinemaRepositoryTest {

	@Mock lateinit var mockService: CinemaService
	@Mock lateinit var mockMapper: CinemaMapper

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
		add(offsetDateTimeRealistic())
	}
	private lateinit var sut: CinemaRepository

	@BeforeEach fun setUp() {
		MockitoAnnotations.initMocks(this)
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
