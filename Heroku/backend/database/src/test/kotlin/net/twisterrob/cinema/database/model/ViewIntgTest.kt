package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.cinema.database.model.test.hasRelationship
import net.twisterrob.test.build
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.loadAll
import org.neo4j.test.mockito.matcher.Neo4jMatchers.hasLabels

@ExtendWith(ModelIntgTestExtension::class)
class ViewIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new view can be loaded back and contains all data`(session: Session) {
		val fixtView: View = fixture.build()
		session.save(fixtView, -1)
		session.clear() // drop cached View objects, start fresh

		val views = session.loadAll<View>(depth = -1)

		assertThat(views, hasSize(1))
		assertThat(views.elementAt(0), sameBeanAs(fixtView))
	}

	@Test fun `new view contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtView: View = fixture.build()
		session.save(fixtView, -1)
		session.clear() // drop cached View objects, start fresh

		graph.beginTx().use {
			val nodes = graph.allNodes.toList()

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
}

fun assertSameData(expected: View, actual: Node) {
	assertThat(actual, hasLabels("View"))
	assertThat(actual.id, equalTo(expected.graphId))
	val expectedProperties = mapOf<String, Any?>(
		"date" to expected.date.toEpochMilli(),
		"class" to expected.className
	)
	assertThat(actual.allProperties, sameBeanAs(expectedProperties))
}
