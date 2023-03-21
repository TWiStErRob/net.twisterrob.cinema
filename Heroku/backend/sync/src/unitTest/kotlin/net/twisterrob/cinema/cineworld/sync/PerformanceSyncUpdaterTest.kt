package net.twisterrob.cinema.cineworld.sync

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Companion.DEFAULT_TIMEZONE
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.that
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Performance as FeedPerformance
import net.twisterrob.cinema.database.model.Performance as DBPerformance

/**
 * @see SyncAppModule.copyPerformanceProperties sut
 * @see DBPerformance.copyPropertiesFrom delegate
 */
@ExtendWith(ModelFixtureExtension::class)
class PerformanceSyncUpdaterTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: Updater<DBPerformance, FeedPerformance>

	@BeforeEach fun setUp() {
		sut = DaggerPerformanceSyncUpdaterTestComponent.create().updater
	}

	@Test fun `updater changes all relevant properties`() {
		val fixtFeed: Feed = fixture.build()
		val targetDB: DBPerformance = fixture.build()
		val fixtIncoming: FeedPerformance = fixture.build()
		val referenceDB: DBPerformance = targetDB.jsonClone()

		sut.invoke(targetDB, fixtIncoming, fixtFeed)

		assertAll {
			// kept
			that("graphId", targetDB.graphId, equalTo(referenceDB.graphId))
			that("className", targetDB.className, equalTo(referenceDB.className))
			// overwritten
			that("booking_url", targetDB.booking_url, equalTo(fixtIncoming.url))
			that("time", targetDB.time, equalTo(fixtIncoming.date.atZoneSimilarLocal(DEFAULT_TIMEZONE)))
			// untested
			that("inCinema", targetDB.inCinema, sameBeanAs(referenceDB.inCinema))
			that("screensFilm", targetDB.screensFilm, sameBeanAs(referenceDB.screensFilm))
		}
	}
}

@Component(modules = [SyncAppModule::class])
private interface PerformanceSyncUpdaterTestComponent {

	val updater: Updater<DBPerformance, FeedPerformance>
}

private inline fun <reified T> T.jsonClone(): T {
	val mapper = JsonMapper().apply {
		registerModule(JavaTimeModule())
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
		disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
	}
	val cloned: T = mapper.readValue(mapper.writeValueAsString(this))
	com.shazam.shazamcrest.MatcherAssert.assertThat(cloned, sameBeanAs(this))
	return cloned
}
