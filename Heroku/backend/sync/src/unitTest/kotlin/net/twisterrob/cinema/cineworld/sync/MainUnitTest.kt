package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import io.ktor.client.HttpClient
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.test.build
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.neo4j.ogm.session.SessionFactory

class MainUnitTest {

	private val mockFeedService: FeedService = mock()
	private val mockCinemaSync: CinemaSync = mock()
	private val mockFilmSync: FilmSync = mock()
	private val mockPerformanceSync: PerformanceSync = mock()
	private val mockNeo4j: SessionFactory = mock()
	private val mockNetwork: HttpClient = mock()

	private val fixture = JFixture()
	private lateinit var sut: Main

	@BeforeEach fun setUp() {
		sut = Main(mockFeedService, mockCinemaSync, mockFilmSync, mockPerformanceSync, mockNeo4j, mockNetwork)
	}

	@Test fun `syncs none when requested`() {
		val input = MainParameters(isSyncCinemas = false, isSyncFilms = false, isSyncPerformances = false)

		sut.sync(input)

		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs cinemas when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(isSyncCinemas = true, isSyncFilms = false, isSyncPerformances = false)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockCinemaSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs cinemas when requested together with performances`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(isSyncCinemas = true, isSyncFilms = false, isSyncPerformances = true)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockCinemaSync).sync(fixtFeed)
		verify(mockPerformanceSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs films when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(isSyncCinemas = false, isSyncFilms = true, isSyncPerformances = false)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockFilmSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs films when requested together with performances`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(isSyncCinemas = false, isSyncFilms = true, isSyncPerformances = true)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockFilmSync).sync(fixtFeed)
		verify(mockPerformanceSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs cinemas and films when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(isSyncCinemas = true, isSyncFilms = true, isSyncPerformances = false)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockFilmSync).sync(fixtFeed)
		verify(mockCinemaSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs all when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(isSyncCinemas = true, isSyncFilms = true, isSyncPerformances = true)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockFilmSync).sync(fixtFeed)
		verify(mockCinemaSync).sync(fixtFeed)
		verify(mockPerformanceSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `sync fails`() {
		val fakeException = RuntimeException("test")
		whenever(mockFeedService.getWeeklyFilmTimes()).thenThrow(fakeException)

		val ex = assertThrows<Throwable> {
			sut.sync(MainParameters(isSyncCinemas = true, isSyncFilms = true, isSyncPerformances = true))
		}
		assertSame(fakeException, ex)

		verify(mockFeedService).getWeeklyFilmTimes()
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `close closes network`() {
		sut.close()

		verify(mockNetwork).close()
	}

	@Test fun `close closes graph`() {
		sut.close()

		verify(mockNeo4j).close()
	}
}
