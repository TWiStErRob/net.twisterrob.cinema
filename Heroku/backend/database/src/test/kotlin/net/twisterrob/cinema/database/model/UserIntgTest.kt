package net.twisterrob.cinema.database.model

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.emptyIterable
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
import org.neo4j.test.mockito.matcher.Neo4jMatchers.hasLabels

@ExtendWith(ModelIntgTestExtension::class)
@TagIntegration
class UserIntgTest {

	private lateinit var fixture: JFixture

	@Test fun `new user can be loaded back and contains all data`(session: Session) {
		val fixtUser: User = fixture.build()
		session.save(fixtUser, -1)
		session.clear() // drop cached User objects, start fresh

		val users = session.loadAll<User>(depth = -1)

		assertThat(users, hasSize(1))
		assertThat(users.elementAt(0), sameBeanAs(fixtUser))
	}

	@Test fun `new user contains the right node information`(session: Session, graph: GraphDatabaseService) {
		val fixtUser: User = fixture.build()
		session.save(fixtUser, -1)
		session.clear() // drop cached User objects, start fresh

		graph.beginTx().use {
			val users = graph.allNodes.toList()

			assertThat(users, hasSize(1))
			val user = users.single()
			assertSameData(fixtUser, user)
			assertThat(user.relationships, emptyIterable())
		}
	}

	@Test fun `empty user fails to save`(session: Session) {
		val user = User()

		assertThrows<UninitializedPropertyAccessException> {
			session.save(user, -1)
		}
	}
}

fun assertSameData(expected: User, actual: Node) = assertAll {
	that("labels", actual, hasLabels("User"))
	that("id", actual.id, equalTo(expected.graphId))
	val expectedProperties = mapOf<String, Any?>(
		"_created" to expected._created.toString(),
		"class" to expected.className,
		"id" to expected.id,
		"name" to expected.name,
		"realm" to expected.realm,
		"email" to expected.email
	)
	that("allProperties", actual.allProperties, sameBeanAs(expectedProperties))
}
