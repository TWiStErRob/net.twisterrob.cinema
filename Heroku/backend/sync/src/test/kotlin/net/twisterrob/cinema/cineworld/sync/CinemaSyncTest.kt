package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.captureSingle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.OffsetDateTime
import net.twisterrob.cinema.database.model.Cinema as DBCinema

@ExtendWith(ModelFixtureExtension::class)
class CinemaSyncTest {

	private val calculator: CinemaSyncCalculator = mock()
	private val dbService: CinemaService = mock()
	private val now by lazy { OffsetDateTime.now() }

	private lateinit var fixture: JFixture
	private lateinit var sut: CinemaSync

	@BeforeEach fun setUp() {
		sut = CinemaSync(calculator, dbService, ::now)
	}

	@Test fun `all changed cinemas are synced properly`() {
		fixture.applyCustomisation {
			add(syncResults<DBCinema>())
		}

		val fixtFeed: Feed = fixture.build()
		val fixtDB: List<DBCinema> = fixture.buildList()
		whenever(dbService.findAll())
			.thenReturn(fixtDB)
		val fixtResult: SyncResults<DBCinema> = fixture.build()
		whenever(calculator.calculate(now, fixtFeed, fixtDB))
			.thenReturn(fixtResult)

		sut.sync(fixtFeed)

		val cinemas: List<DBCinema> = captureSingle {
			verify(dbService).save(capture())
		}
		val persistedCinemas = fixtResult.insert + fixtResult.delete + fixtResult.update + fixtResult.restore
		assertThat(cinemas, containsInAnyOrder(*persistedCinemas.toTypedArray()))
	}
}
