package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.javaURIRealistic
import net.twisterrob.test.that
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Performance as FeedPerformance
import net.twisterrob.cinema.database.model.Performance as DBPerformance

/**
 * @see SyncAppModule.cinemaEntityFactory sut
 * @see DBPerformance.copyPropertiesFrom delegate
 */
class PerformanceSyncCreatorUnitTest {

	private val fixture = JFixture().applyCustomisation {
		add(javaURIRealistic())
	}
	private lateinit var sut: Creator<FeedPerformance, DBPerformance>

	@BeforeEach fun setUp() {
		sut = DaggerPerformanceSyncCreatorTestComponent.create().creator
	}

	@Test fun `creator copies all properties`() {
		val fixtFeed: Feed = fixture.build()
		val fixtPerformance: FeedPerformance = fixture.build()

		val actualPerformance = sut.invoke(fixtPerformance, fixtFeed)

		assertAll {
			that("className", actualPerformance.className, equalTo("Performance"))
			// default empty values
			that("graphId", actualPerformance.graphId, nullValue())
			// changed values
			that("booking_url", actualPerformance.booking_url, equalTo(fixtPerformance.url))
		}
	}
}

@Component(modules = [SyncAppModule::class])
private interface PerformanceSyncCreatorTestComponent {

	val creator: Creator<FeedPerformance, DBPerformance>
}
