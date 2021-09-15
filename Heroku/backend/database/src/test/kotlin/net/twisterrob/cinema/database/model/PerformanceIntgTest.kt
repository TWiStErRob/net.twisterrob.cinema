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
class PerformanceIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new performance can be loaded back and contains all data`(session: Session) {
		val fixtPerformance: Performance = fixture.build()
		val expected = fixtPerformance.copy().apply {
			graphId = 1
			inUTC()
		}
		session.save(fixtPerformance, -1)
		session.clear() // drop cached Performance objects, start fresh

		val performances = session.loadAll<Performance>(depth = -1)

		assertThat(performances, hasSize(1))
		assertThat(performances.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `new performance contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtPerformance: Performance = fixture.build()
		session.save(fixtPerformance, -1)
		session.clear() // drop cached Performance objects, start fresh

		graph.beginTx().use { tx ->
			val nodes = tx.allNodes.toList()

			assertThat(nodes, hasSize(3))
			val (film, performance, cinema) = nodes
			assertSameData(fixtPerformance, performance)
			assertSameData(fixtPerformance.screensFilm, film)
			assertSameData(fixtPerformance.inCinema, cinema)
			assertThat(
				performance.relationships, containsInAnyOrder(
					hasRelationship(performance, "IN", cinema),
					hasRelationship(performance, "SCREENS", film),
				)
			)
		}
	}

	@Test fun `empty performance fails to save`(session: Session) {
		val performance = Performance()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(performance, -1)
		}
	}
}

fun Performance.inUTC() {
	inCinema.inUTC()
	screensFilm.inUTC()
}

fun assertSameData(expected: Performance, actual: Node) = assertAll {
	that("labels", actual, hasLabels("Performance"))
	that("id", actual.id, equalTo(expected.graphId))
	val expectedProperties = mapOf(
		"class" to expected.className,
		"time" to expected.time,
		"booking_url" to expected.booking_url,
	)
	that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
}
