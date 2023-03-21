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
import net.twisterrob.cinema.database.model.ModelFixtureExtension
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.that
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Film as FeedFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

/**
 * @see SyncAppModule.copyFilmProperties sut
 * @see DBFilm.copyPropertiesFrom delegate
 */
@ExtendWith(ModelFixtureExtension::class)
class FilmSyncUpdaterUnitTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: Updater<DBFilm, FeedFilm>

	@BeforeEach fun setUp() {
		sut = DaggerFilmSyncUpdaterTestComponent.create().updater
	}

	@Test fun `updater changes all relevant properties`() {
		val fixtFeed: Feed = fixture.build()
		val targetDB: DBFilm = fixture.build()
		val fixtIncoming: FeedFilm = fixture.build()
		val referenceDB: DBFilm = targetDB.jsonClone()

		sut.invoke(targetDB, fixtIncoming, fixtFeed)

		assertAll {
			// kept
			that("graphId", targetDB.graphId, equalTo(referenceDB.graphId))
			that("className", targetDB.className, equalTo(referenceDB.className))
			that("_created", targetDB._created.toInstant(), equalTo(referenceDB._created.toInstant()))
			that("_updated", targetDB._updated?.toInstant(), equalTo(referenceDB._updated?.toInstant()))
			that("_deleted", targetDB._deleted?.toInstant(), equalTo(referenceDB._deleted?.toInstant()))
			// overwritten
			that("edi", targetDB.edi, equalTo(fixtIncoming.id))
			that("originalTitle", targetDB.originalTitle, equalTo(fixtIncoming.title))
			that("director", targetDB.director, equalTo(fixtIncoming.director))
			that("actors", targetDB.actors, equalTo(fixtIncoming.cast))
			that("film_url", targetDB.film_url, equalTo(fixtIncoming.url))
			that("poster_url", targetDB.poster_url, equalTo(fixtIncoming.posterUrl))
			that("runtime", targetDB.runtime, equalTo(fixtIncoming.runningTime.toLong()))
			that("trailer", targetDB.trailer, equalTo(fixtIncoming.trailerUrl))
			that("cert", targetDB.cert, equalTo(fixtIncoming.classification))
			that("classification", targetDB.classification, equalTo(fixtIncoming.classification))
			// complex
			that("slug", targetDB.slug, equalTo(fixtIncoming.url.path.substringAfterLast("/")))
			that("title", targetDB.title, not(equalTo(referenceDB.title)))
			that("release", targetDB.release, not(equalTo(referenceDB.release)))
			that("format", targetDB.format, not(equalTo(referenceDB.format)))
			//that("imax", targetDB.imax, not(equalTo(referenceDB.imax)))
			//that("3D", targetDB.`3D`, not(equalTo(referenceDB.`3D`)))
			// untested
			that("views", targetDB.views, equalTo(referenceDB.views))
		}
	}
}

@Component(modules = [SyncAppModule::class])
private interface FilmSyncUpdaterTestComponent {

	val updater: Updater<DBFilm, FeedFilm>
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
