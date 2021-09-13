package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.ktor.client.HttpClient
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.test.build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.ogm.session.SessionFactory

class MainTest {

	private val mockFeedService: FeedService = mock()
	private val mockCinemaSync: CinemaSync = mock()
	private val mockFilmSync: FilmSync = mock()
	private val mockNeo4j: SessionFactory = mock()
	private val mockNetwork: HttpClient = mock()

	private val fixture = JFixture()
	private lateinit var sut: Main

	@BeforeEach fun setUp() {
		sut = Main(mockFeedService, mockCinemaSync, mockFilmSync, mockNeo4j, mockNetwork)
	}

	@Test fun `syncs cinemas when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(syncCinemas = true, syncFilms = false)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockCinemaSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs films when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(syncCinemas = false, syncFilms = true)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockFilmSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs all when requested`() {
		val fixtFeed: Feed = fixture.build()
		whenever(mockFeedService.getWeeklyFilmTimes()).thenReturn(fixtFeed)
		val input = MainParameters(syncCinemas = true, syncFilms = true)

		sut.sync(input)

		verify(mockFeedService).getWeeklyFilmTimes()
		verify(mockFilmSync).sync(fixtFeed)
		verify(mockCinemaSync).sync(fixtFeed)
		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}

	@Test fun `syncs none when requested`() {
		val input = MainParameters(syncCinemas = false, syncFilms = false)

		sut.sync(input)

		verifyNoMoreInteractions(mockFeedService, mockCinemaSync, mockFeedService)
	}
}
