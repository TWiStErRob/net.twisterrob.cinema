package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.neo4j.ogm.TimestampConverter
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.emptyIterable
import net.twisterrob.test.neo4j.mockito.hasLabels
import net.twisterrob.test.that
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import java.time.ZoneOffset

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
@TagIntegration
class CinemaIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new cinema can be loaded back and contains all data`(session: Session) {
		val fixtCinema: Cinema = fixture.build()
		val expected = fixtCinema.copy().apply {
			graphId = 0
			inUTC()
		}
		session.save(fixtCinema, -1)
		session.clear() // drop cached Cinema objects, start fresh

		val cinemas = session.loadAll<Cinema>(depth = -1)

		assertThat(cinemas, hasSize(1))
		assertThat(cinemas.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `new cinema contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtCinema: Cinema = fixture.build()
		session.save(fixtCinema, -1)
		session.clear() // drop cached Cinema objects, start fresh

		graph.beginTx().use { tx ->
			val cinemas = tx.allNodes.toList()

			assertThat(cinemas, hasSize(1))
			val cinema = cinemas.single()
			assertSameData(fixtCinema, cinema)
			assertThat(cinema.relationships, emptyIterable())
		}
	}

	@Test fun `cinema can be undeleted`(session: Session) {
		val fixtCinema: Cinema = fixture.build()
		val expected = fixtCinema.copy().apply {
			graphId = 0
			inUTC()
			_deleted = null
		}
		session.save(fixtCinema, -1)
		session.clear() // drop cached Cinema objects, start fresh

		fixtCinema._deleted = null
		session.save(fixtCinema, -1)
		session.clear() // drop cached Cinema objects, start fresh

		val cinemas = session.loadAll<Cinema>(depth = -1)

		assertThat(cinemas, hasSize(1))
		assertThat(cinemas.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `empty cinema fails to save`(session: Session) {
		val cinema = Cinema()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(cinema, -1)
		}
	}
}

fun Cinema.inUTC() {
	_created = _created.withOffsetSameInstant(ZoneOffset.UTC)
	_updated = _updated?.withOffsetSameInstant(ZoneOffset.UTC)
	_deleted = _deleted?.withOffsetSameInstant(ZoneOffset.UTC)
}

fun assertSameData(expected: Cinema, actual: Node) {
	assertAll {
		that("labels", actual, hasLabels("Cinema"))
		@Suppress("DEPRECATION", "removal") // TODEL https://github.com/neo4j/neo4j-ogm/issues/924
		that("id", actual.id, equalTo(expected.graphId))
		val expectedProperties = mapOf(
			"_created" to TimestampConverter().toGraphProperty(expected._created),
			"_updated" to TimestampConverter().toGraphProperty(expected._updated),
			"_deleted" to TimestampConverter().toGraphProperty(expected._deleted),
			"class" to expected.className,
			"cineworldID" to expected.cineworldID,
			"name" to expected.name,
			"postcode" to expected.postcode,
			"address" to expected.address,
			"telephone" to expected.telephone,
			"cinema_url" to expected.cinema_url
		)
		that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
	}
}
