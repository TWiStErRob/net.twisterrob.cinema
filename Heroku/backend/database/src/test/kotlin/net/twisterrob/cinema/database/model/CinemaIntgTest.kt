package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.test.build
import net.twisterrob.test.emptyIterable
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Label.label
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll

@ExtendWith(ModelIntgTestExtension::class)
class CinemaIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new cinema can be loaded back and contains all data`(session: Session) {
		val fixtCinema: Cinema = fixture.build()
		session.save(fixtCinema, -1)
		session.clear() // drop cached Cinema objects, start fresh

		val cinemas = session.loadAll<Cinema>(depth = -1)

		assertThat(cinemas, hasSize(1))
		assertThat(cinemas.elementAt(0), sameBeanAs(fixtCinema))
	}

	@Test fun `new cinema contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtCinema: Cinema = fixture.build()
		session.save(fixtCinema, -1)
		session.clear() // drop cached Cinema objects, start fresh

		graph.beginTx().use {
			val cinemas = graph.allNodes.toList()

			assertThat(cinemas, hasSize(1))
			val cinema = cinemas.elementAt(0)
			assertThat(cinema.labels.toSet(), equalTo(setOf(label("Cinema"))))
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
