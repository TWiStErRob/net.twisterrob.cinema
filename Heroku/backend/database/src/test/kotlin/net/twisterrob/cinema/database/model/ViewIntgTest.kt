package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.cinema.database.model.test.hasRelationship
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
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
import org.neo4j.test.mockito.matcher.Neo4jMatchers.hasLabels
import java.time.temporal.ChronoUnit

@ExtendWith(ModelIntgTestExtension::class)
@TagIntegration
class ViewIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new view can be loaded back and contains all data`(session: Session) {
		val fixtView: View = fixture.build()
		val expected = fixtView.copy().apply view@{
			graphId = 2
			// Java 9+ is more precise than Java 8, but we only store milliseconds.
			date = date.truncatedTo(ChronoUnit.MILLIS)
			atCinema = atCinema.copy().apply {
				graphId = 3
				views = mutableSetOf(this@view)
			}
			watchedFilm = watchedFilm.copy().apply {
				graphId = 1
				views = mutableSetOf(this@view)
			}
			userRef = userRef.copy().apply {
				graphId = 0
				views = mutableSetOf(this@view)
			}
		}
		session.save(fixtView, -1)
		session.clear() // drop cached View objects, start fresh

		val views = session.loadAll<View>(depth = -1)

		assertThat(views, hasSize(1))
		assertThat(views.elementAt(0), sameBeanAs(expected))
	}

	@Test fun `new view contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtView: View = fixture.build()
		session.save(fixtView, -1)
		session.clear() // drop cached View objects, start fresh

		graph.beginTx().use { tx ->
			val nodes = tx.allNodes.toList()

			assertThat(nodes, hasSize(4))
			val (user, film, view, cinema) = nodes
			assertSameData(fixtView, view)
			assertSameData(fixtView.userRef, user)
			assertSameData(fixtView.watchedFilm, film)
			assertSameData(fixtView.atCinema, cinema)
			assertThat(
				view.relationships, containsInAnyOrder(
					hasRelationship(view, "AT", cinema),
					hasRelationship(view, "WATCHED", film),
					hasRelationship(user, "ATTENDED", view)
				)
			)
		}
	}

	@Test fun `empty view fails to save`(session: Session) {
		val view = View()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(view, -1)
		}
	}
}

fun assertSameData(expected: View, actual: Node) = assertAll {
	that("labels", actual, hasLabels("View"))
	that("id", actual.id, equalTo(expected.graphId))
	val expectedProperties = mapOf<String, Any?>(
		"date" to expected.date.toEpochMilli(),
		"class" to expected.className
	)
	that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
}
