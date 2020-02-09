package net.twisterrob.cinema.database.model

import com.flextrade.kfixture.KFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import dagger.Component
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.services.CinemaServices
import net.twisterrob.test.emptyIterable
import net.twisterrob.test.offsetDateTimeRealistic
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.graphdb.Label.label
import org.neo4j.harness.ServerControls
import org.neo4j.harness.TestServerBuilders
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll

class CinemaIntgTest {

	private lateinit var testServer: ServerControls
	private lateinit var session: Session

	@BeforeEach fun setUp() {
		testServer = TestServerBuilders.newInProcessBuilder().newServer()

		val dagger = DaggerCinemaIntgTestComponent
			.builder()
			.graphDBUri(testServer.boltURI())
			.build()

		session = dagger.session
	}

	@AfterEach fun tearDown() {
		testServer.graph().shutdown()
	}

	@Test fun `new cinema can be loaded back and contains all data`() {
		val fixtCinema: Cinema = KFixture {
			add(validDBData())
			add(offsetDateTimeRealistic())
		}()
		session.save(fixtCinema)
		session.clear() // drop cached Cinema objects, start fresh

		val cinemas = session.loadAll<Cinema>(depth = -1)

		assertThat(cinemas, hasSize(1))
		assertThat(cinemas.elementAt(0), sameBeanAs(fixtCinema))
	}

	@Test fun `new cinema contains the right node information`() {
		val fixtCinema: Cinema = KFixture {
			add(validDBData())
			add(offsetDateTimeRealistic())
		}()
		session.save(fixtCinema)
		session.clear() // drop cached Cinema objects, start fresh

		testServer.graph().beginTx().use {
			val cinemas = testServer.graph().allNodes.toList()

			assertThat(cinemas, hasSize(1))
			val cinema = cinemas.elementAt(0)
			assertThat(cinema.labels.toSet(), equalTo(setOf(label("Cinema"), label("BaseNode"))))
			assertThat(cinema.id, equalTo(fixtCinema.graphId))
			val expectedProperties = mapOf(
				"_created" to fixtCinema._created.toString(),
				"_updated" to fixtCinema._updated.toString(),
				"_deleted" to fixtCinema._deleted.toString(),
				"class" to fixtCinema.className,
				"cineworldID" to fixtCinema.cineworldID,
				"name" to fixtCinema.name,
				"postcode" to fixtCinema.postcode,
				"address" to fixtCinema.address,
				"telephone" to fixtCinema.telephone,
				"cinema_url" to fixtCinema.cinema_url
			)
			assertThat(cinema.allProperties, equalTo(expectedProperties))
			assertThat(cinema.relationships, emptyIterable())
		}
	}
}

@Component(modules = [Neo4JModule::class])
@Neo4J
private interface CinemaIntgTestComponent : CinemaServices {

	val session: Session

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): CinemaIntgTestComponent
	}
}
