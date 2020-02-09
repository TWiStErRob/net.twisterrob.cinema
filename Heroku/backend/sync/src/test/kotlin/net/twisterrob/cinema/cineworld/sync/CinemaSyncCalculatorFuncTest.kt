package net.twisterrob.cinema.cineworld.sync

import com.flextrade.kfixture.KFixture
import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.test.build
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

class CinemaSyncCalculatorFuncTest {

	private val fixture = KFixture()
	private lateinit var sut: CinemaSyncCalculator

	@BeforeEach fun setUp() {
		val dagger = DaggerCinemaSyncCalculatorFuncTestComponent.create()

		sut = CinemaSyncCalculator(dagger.syncer)
	}

	@Test fun `no cinemas in feed result in no data synced`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val db = emptyList<DBCinema>()

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertThat(result.insert, empty())
		assertThat(result.update, empty())
		assertThat(result.delete, empty())
		assertThat(result.alreadyDeleted, empty())
		assertThat(result.restore, empty())
	}

	@Test fun `new cinemas in feed result in added cinemas`() {
		val fixtCinema: FeedCinema = fixture.build()
		val feed = Feed(emptyList(), listOf(fixtCinema), emptyList(), emptyList())
		val db = emptyList<DBCinema>()

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertThat(result.insert, hasSize(1))
		assertEquals(fixtCinema.id, result.insert.single().cineworldID)
		assertThat(result.update, empty())
		assertThat(result.delete, empty())
		assertThat(result.alreadyDeleted, empty())
		assertThat(result.restore, empty())
	}
}

@Component(modules = [SyncAppModule::class])
private interface CinemaSyncCalculatorFuncTestComponent {

	val syncer: NodeSyncer<FeedCinema, DBCinema>
}
