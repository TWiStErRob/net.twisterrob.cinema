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
			val user = users.elementAt(0)
			assertThat(user.labels.toSet(), equalTo(setOf(label("User"))))
			assertThat(user.id, equalTo(fixtUser.graphId))
			val expectedProperties = mapOf<String, Any?>(
				"_created" to fixtUser._created.toString(),
				"class" to fixtUser.className,
				"id" to fixtUser.id,
				"name" to fixtUser.name,
				"realm" to fixtUser.realm,
				"email" to fixtUser.email
			)
			assertThat(user.allProperties, equalTo(expectedProperties))
			assertThat(user.relationships, emptyIterable())
		}
	}
}
