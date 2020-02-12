package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import net.twisterrob.cinema.database.model.Cinema as DBCinema

class CinemaSyncTest {
	private val calculator: CinemaSyncCalculator = mock()
	private val feedService: FeedService = mock()
	private val dbService: CinemaService = mock()
	private val now by lazy { OffsetDateTime.now() }

	private val fixture = JFixture()
	private lateinit var sut: CinemaSync

	@BeforeEach fun setUp() {
		sut = CinemaSync(calculator, feedService, dbService, ::now)
	}

	@Test fun `all changed cinemas are synced properly`() {
		fixture.applyCustomisation {
			add(syncResults<DBCinema>())
			add(validDBData())
		}

		val fixtFeed: Feed = fixture.build()
		whenever(feedService.getWeeklyFilmTimes())
			.thenReturn(fixtFeed)
		val fixtDB: List<DBCinema> = fixture.buildList()
		whenever(dbService.findAll())
			.thenReturn(fixtDB)
		val fixtResult: SyncResults<DBCinema> = fixture.build()
		whenever(calculator.calculate(now, fixtFeed, fixtDB))
			.thenReturn(fixtResult)

		sut.sync()

		argumentCaptor<List<DBCinema>> {
			verify(dbService).save(capture())
			val cinemas = allValues.single()
			val persistedCinemas = fixtResult.insert + fixtResult.delete + fixtResult.update + fixtResult.restore
			assertThat(cinemas, containsInAnyOrder(*persistedCinemas.toTypedArray()))
		}
	}
}
