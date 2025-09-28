package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.cinema.database.model.test.hasRelationship
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.neo4j.allNodes
import net.twisterrob.test.neo4j.mockito.hasLabels
import net.twisterrob.test.neo4j.allProperties
import net.twisterrob.test.neo4j.id
import net.twisterrob.test.neo4j.relationships
import net.twisterrob.test.neo4j.session
import net.twisterrob.test.that
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.types.Node
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@ExtendWith(ModelIntgTestExtension::class, ModelFixtureExtension::class)
@Testcontainers(disabledWithoutDocker = true)
class PerformanceIntgTest {

	@Container
	private val neo4jContainer = Neo4jContainer(DockerImageName.parse("neo4j:2025.07.1"))
		.withoutAuthentication()

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

	@Test fun `new performance contains the right node information`(session: Session) {
		val graph = GraphDatabase.driver(neo4jContainer.boltUrl)
		val fixtPerformance: Performance = fixture.build()
		session.save(fixtPerformance, -1)
		session.clear() // drop cached Performance objects, start fresh

		graph.session {
			val nodes = this.allNodes.toList()

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

fun assertSameData(expected: Performance, actual: Node) {
	assertAll {
		that("labels", actual, hasLabels("Performance"))
		@Suppress("DEPRECATION", "removal") // TODEL https://github.com/neo4j/neo4j-ogm/issues/924
		that("id", actual.id, equalTo(expected.graphId))
		val expectedProperties = mapOf(
			"class" to expected.className,
			"time" to expected.time,
			"booking_url" to expected.booking_url,
		)
		that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
	}
}
