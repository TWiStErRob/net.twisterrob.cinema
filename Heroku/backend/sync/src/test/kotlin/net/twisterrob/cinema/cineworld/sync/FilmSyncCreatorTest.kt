package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.that
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Film as FeedFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

/**
 * @see SyncAppModule.cinemaEntityFactory sut
 * @see DBFilm.copyPropertiesFrom delegate
 */
class FilmSyncCreatorTest {

	private val fixture = JFixture()
	private lateinit var sut: Creator<FeedFilm, DBFilm>

	@BeforeEach fun setUp() {
		sut = DaggerFilmSyncCreatorTestComponent.create().creator
	}

	@Test fun `creator copies all properties`() {
		val fixtFeed: Feed = fixture.build()
		val fixtFilm: FeedFilm = fixture.build()

		val actualFilm = sut.invoke(fixtFilm, fixtFeed)

		assertAll {
			that("className", actualFilm.className, equalTo("Film"))
			// default empty values
			that("graphId", actualFilm.graphId, nullValue())
			that("_updated", actualFilm._updated, nullValue())
			that("_deleted", actualFilm._deleted, nullValue())
			o { assertThrows<UninitializedPropertyAccessException>("_created") { actualFilm._created } }
			that("views", actualFilm.views, empty())
			// changed values
			that("edi", actualFilm.edi, equalTo(fixtFilm.id))
			that("originalTitle", actualFilm.originalTitle, equalTo(fixtFilm.title))
			that("director", actualFilm.director, equalTo(fixtFilm.director))
			that("actors", actualFilm.actors, equalTo(fixtFilm.cast))
			that("film_url", actualFilm.film_url, equalTo(fixtFilm.url))
			that("poster_url", actualFilm.poster_url, equalTo(fixtFilm.posterUrl))
			that("runtime", actualFilm.runtime, equalTo(fixtFilm.runningTime.toLong()))
			that("trailer", actualFilm.trailer, equalTo(fixtFilm.trailerUrl))
			that("cert", actualFilm.cert, equalTo(fixtFilm.classification))
			that("classification", actualFilm.classification, equalTo(fixtFilm.classification))
		}
	}
}

@Component(modules = [SyncAppModule::class])
private interface FilmSyncCreatorTestComponent {

	val creator: Creator<FeedFilm, DBFilm>
}
