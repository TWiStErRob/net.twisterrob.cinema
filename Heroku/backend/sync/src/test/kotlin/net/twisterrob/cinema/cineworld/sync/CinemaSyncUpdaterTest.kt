package net.twisterrob.cinema.cineworld.sync

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import dagger.Component
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.assertAll
import net.twisterrob.test.that
import net.twisterrob.test.build
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

class CinemaSyncUpdaterTest {

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
	}
	private lateinit var sut: Updater<DBCinema, FeedCinema>

	@BeforeEach fun setUp() {
		sut = DaggerCinemaSyncUpdaterTestComponent.create().updater
	}

	@Test fun `updater changes all relevant properties`() {
		val fixtFeed: FeedCinema = fixture.build()
		val targetDB: DBCinema = fixture.build()
		val referenceDB: DBCinema = targetDB.jsonClone()

		sut.invoke(targetDB, fixtFeed)

		assertAll {
			// kept
			that("graphId", targetDB.graphId, equalTo(referenceDB.graphId))
			that("className", targetDB.className, equalTo(referenceDB.className))
			that("_created", targetDB._created.toInstant(), equalTo(referenceDB._created.toInstant()))
			that("_updated", targetDB._updated?.toInstant(), equalTo(referenceDB._updated?.toInstant()))
			that("_deleted", targetDB._deleted?.toInstant(), equalTo(referenceDB._deleted?.toInstant()))
			// overwritten
			that("cineworldID", targetDB.cineworldID, equalTo(fixtFeed.id))
			that("name", targetDB.name, equalTo(fixtFeed.name))
			that("postcode", targetDB.postcode, equalTo(fixtFeed.postcode))
			that("address", targetDB.address, equalTo(fixtFeed.address))
			that("telephone", targetDB.telephone, equalTo(fixtFeed.phone))
			that("cinema_url", targetDB.cinema_url, equalTo(fixtFeed.url))
			// untested
			that("users", targetDB.users, equalTo(referenceDB.users))
			that("views", targetDB.views, equalTo(referenceDB.views))
		}
	}
}

@Component(modules = [SyncAppModule::class])
private interface CinemaSyncUpdaterTestComponent {

	val creator: Creator<FeedCinema, DBCinema>
	val updater: Updater<DBCinema, FeedCinema>
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
