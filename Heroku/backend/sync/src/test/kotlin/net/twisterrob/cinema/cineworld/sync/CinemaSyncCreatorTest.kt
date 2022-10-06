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
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

/**
 * @see SyncAppModule.cinemaEntityFactory sut
 * @see DBCinema.copyPropertiesFrom delegate
 */
class CinemaSyncCreatorTest {

	private val fixture = JFixture()
	private lateinit var sut: Creator<FeedCinema, DBCinema>

	@BeforeEach fun setUp() {
		sut = DaggerCinemaSyncCreatorTestComponent.create().creator
	}

	@Test fun `creator copies all properties`() {
		val fixtFeed: Feed = fixture.build()
		val fixtCinema: FeedCinema = fixture.build()

		val actualCinema = sut.invoke(fixtCinema, fixtFeed)

		assertAll {
			that("className", actualCinema.className, equalTo("Cinema"))
			// default empty values
			that("graphId", actualCinema.graphId, nullValue())
			that("_updated", actualCinema._updated, nullValue())
			that("_deleted", actualCinema._deleted, nullValue())
			o { assertThrows<UninitializedPropertyAccessException>("_created") { actualCinema._created } }
			that("users", actualCinema.users, empty())
			that("views", actualCinema.views, empty())
			// changed values
			that("cineworldID", actualCinema.cineworldID, equalTo(fixtCinema.id))
			that("name", actualCinema.name, equalTo(fixtCinema.name))
			that("postcode", actualCinema.postcode, equalTo(fixtCinema.postcode))
			that("address", actualCinema.address, equalTo(fixtCinema.address))
			that("telephone", actualCinema.telephone, equalTo(fixtCinema.phone))
			that("cinema_url", actualCinema.cinema_url, equalTo(fixtCinema.url))
		}
	}
}

@Component(modules = [SyncAppModule::class])
private interface CinemaSyncCreatorTestComponent {

	val creator: Creator<FeedCinema, DBCinema>
}
