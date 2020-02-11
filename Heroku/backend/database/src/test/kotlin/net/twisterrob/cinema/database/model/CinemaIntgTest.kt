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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import org.neo4j.test.mockito.matcher.Neo4jMatchers.hasLabels

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
			val cinema = cinemas.single()
			assertSameData(fixtCinema, cinema)
			assertThat(cinema.relationships, emptyIterable())
		}
	}

	@Test fun `empty cinema fails to save`(session: Session) {
		val cinema = Cinema()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(cinema, -1)
		}
	}
}

fun assertSameData(expected: Cinema, actual: Node) {
	assertThat(actual, hasLabels("Cinema"))
	assertThat(actual.id, equalTo(expected.graphId))
	val expectedProperties = mapOf(
		"_created" to expected._created.toString(),
		"_updated" to expected._updated.toString(),
		"_deleted" to expected._deleted.toString(),
		"class" to expected.className,
		"cineworldID" to expected.cineworldID,
		"name" to expected.name,
		"postcode" to expected.postcode,
		"address" to expected.address,
		"telephone" to expected.telephone,
		"cinema_url" to expected.cinema_url
	)
	assertThat(actual.allProperties, sameBeanAs(expectedProperties))
}
