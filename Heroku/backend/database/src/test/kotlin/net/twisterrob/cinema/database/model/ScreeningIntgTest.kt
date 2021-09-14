package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.cinema.database.model.test.hasRelationship
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.neo4j.mockito.hasLabels
import net.twisterrob.test.that
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll

@ExtendWith(ModelIntgTestExtension::class)
@TagIntegration
class ScreeningIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new screening can be loaded back and contains all data`(session: Session) {
		val fixtScreening: Screening = fixture.build()
		val expected = fixtScreening.copy().apply {
			graphId = 0
			inUTC()
		}
		session.save(fixtScreening, -1)
		session.clear() // drop cached Screening objects, start fresh

		val screenings = session.loadAll<Screening>(depth = -1)

		assertThat(screenings, hasSize(1))
		assertThat(screenings.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `new screening contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtScreening: Screening = fixture.build()
		session.save(fixtScreening, -1)
		session.clear() // drop cached Screening objects, start fresh

		graph.beginTx().use { tx ->
			val nodes = tx.allNodes.toList()

			assertThat(nodes, hasSize(3))
			val (screening, film, cinema) = nodes
			assertSameData(fixtScreening, screening)
			assertSameData(fixtScreening.film, film)
			assertSameData(fixtScreening.cinema, cinema)
			assertThat(
				screening.relationships, containsInAnyOrder(
					hasRelationship(screening, "AT", cinema),
					hasRelationship(screening, "SCREENS", film),
				)
			)
		}
	}

	@Test fun `empty screening fails to save`(session: Session) {
		val screening = Screening()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(screening, -1)
		}
	}
}

fun Screening.inUTC() {
	cinema.inUTC()
	film.inUTC()
}

fun assertSameData(expected: Screening, actual: Node) = assertAll {
	that("labels", actual, hasLabels("Screening"))
	that("id", actual.id, equalTo(expected.graphId))
	val expectedProperties = mapOf(
		"class" to expected.className,
		"time" to expected.time,
		"booking_url" to expected.booking_url,
	)
	that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
}
