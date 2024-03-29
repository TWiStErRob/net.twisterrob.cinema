package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.cinema.database.services.FilmService
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.captureSingle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import net.twisterrob.cinema.database.model.Film as DBFilm

@ExtendWith(ModelFixtureExtension::class)
class FilmSyncUnitTest {

	private val calculator: FilmSyncCalculator = mock()
	private val dbService: FilmService = mock()
	private val now by lazy { OffsetDateTime.now() }

	private lateinit var fixture: JFixture
	private lateinit var sut: FilmSync

	@BeforeEach fun setUp() {
		sut = FilmSync(calculator, dbService, ::now)
	}

	@Test fun `all changed cinemas are synced properly`() {
		fixture.applyCustomisation {
			add(syncResults<DBFilm>())
		}

		val fixtFeed: Feed = fixture.build()
		val fixtDB: List<DBFilm> = fixture.buildList()
		whenever(dbService.findAll())
			.thenReturn(fixtDB)
		val fixtResult: SyncResults<DBFilm> = fixture.build()
		whenever(calculator.calculate(now, fixtFeed, fixtDB))
			.thenReturn(fixtResult)

		sut.sync(fixtFeed)

		val films: List<DBFilm> = captureSingle {
			verify(dbService).save(capture())
		}
		val persistedFilms = fixtResult.insert + fixtResult.delete + fixtResult.update + fixtResult.restore
		assertThat(films, containsInAnyOrder(*persistedFilms.toTypedArray()))
	}
}
