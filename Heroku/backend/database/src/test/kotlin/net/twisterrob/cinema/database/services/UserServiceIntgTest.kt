package net.twisterrob.cinema.database.services

import com.flextrade.jfixture.JFixture
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import net.twisterrob.cinema.database.model.User
import net.twisterrob.cinema.database.model.assertSameData
import net.twisterrob.cinema.database.model.test.ModelIntgTestExtension
import net.twisterrob.neo4j.ogm.load
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.emptyIterable
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.ogm.session.Session
import java.time.OffsetDateTime

@ExtendWith(ModelIntgTestExtension::class)
@TagIntegration
class UserServiceIntgTest {

	private lateinit var fixture: JFixture
	private lateinit var sut: UserService

	@BeforeEach fun setUp(session: Session) {
		sut = UserService(session)
	}

	@Test fun `find() finds user by external ID`(session: Session) {
		val fixtUsers: List<User> = fixture.buildList(size = 3)
		fixtUsers.forEach(session::save)

		val result = sut.find(fixtUsers[1].id)

		assertNotNull(result)
		assertThat(result, sameBeanAs(fixtUsers[1]))
	}

	@Test fun `find() doesn't find non-existent user`(session: Session) {
		val fixtUsers: List<User> = fixture.buildList()
		fixtUsers.forEach(session::save)

		val result = sut.find(fixture.build())

		assertNull(result)
	}

	@Test fun `addUser() saves all data`(session: Session) {
		val fixtUserId: String = fixture.build()
		val fixtEmail: String = fixture.build()
		val fixtName: String = fixture.build()
		val fixtRealm: String = fixture.build()
		val fixtCreated: OffsetDateTime = fixture.build()

		val result = sut.addUser(fixtUserId, fixtEmail, fixtName, fixtRealm, fixtCreated)

		assertThat(result.id, equalTo(fixtUserId))
		assertThat(result.email, equalTo(fixtEmail))
		assertThat(result.name, equalTo(fixtName))
		assertThat(result.realm, equalTo(fixtRealm))
		assertThat(result._created, equalTo(fixtCreated))

		val data: User? = session.load(result.id, 1)
		assertThat("Data in database is the same as just added User", data, sameBeanAs(result))
	}

	@Test fun `addUser() does not create a new identical user`(session: Session, graph: GraphDatabaseService) {
		val fixtUserId: String = fixture.build()
		val fixtEmail: String = fixture.build()
		val fixtName: String = fixture.build()
		val fixtRealm: String = fixture.build()
		val fixtCreated: OffsetDateTime = fixture.build()
		val savedUser = User().apply {
			this.id = fixtUserId
			this.email = fixtEmail
			this.name = fixtName
			this.realm = fixtRealm
			this._created = fixtCreated
		}
		session.save(savedUser)

		val result = sut.addUser(fixtUserId, fixtEmail, fixtName, fixtRealm, fixtCreated)

		assertThat(result.id, equalTo(fixtUserId))
		assertThat(result.email, equalTo(fixtEmail))
		assertThat(result.name, equalTo(fixtName))
		assertThat(result.realm, equalTo(fixtRealm))
		assertThat(result._created, equalTo(fixtCreated))
		assertThat(result.graphId, equalTo(savedUser.graphId))

		graph.beginTx().use { tx ->
			val users = tx.allNodes.toList()

			assertThat(users, hasSize(1))
			users.single().let { userNode ->
				assertSameData(savedUser, userNode)
				assertThat(userNode.relationships, emptyIterable())
			}
		}
	}

	@Test fun `addUser() does create a new totally different user`(session: Session, graph: GraphDatabaseService) {
		val fixtUserId: String = fixture.build()
		val fixtEmail: String = fixture.build()
		val fixtName: String = fixture.build()
		val fixtRealm: String = fixture.build()
		val fixtCreated: OffsetDateTime = fixture.build()
		val fixtUser: User = fixture.build()
		session.save(fixtUser)

		val result = sut.addUser(fixtUserId, fixtEmail, fixtName, fixtRealm, fixtCreated)

		assertThat(result.id, equalTo(fixtUserId))
		assertThat(result.email, equalTo(fixtEmail))
		assertThat(result.name, equalTo(fixtName))
		assertThat(result.realm, equalTo(fixtRealm))
		assertThat(result._created, equalTo(fixtCreated))
		assertThat(result.graphId, not(equalTo(fixtUser.graphId)))

		val expectedNewUser = User().apply {
			this.graphId = result.graphId
			this.id = fixtUserId
			this.email = fixtEmail
			this.name = fixtName
			this.realm = fixtRealm
			this._created = fixtCreated
		}
		graph.beginTx().use { tx ->
			val users = tx.allNodes.toList()

			assertThat(users, hasSize(2))
			users[0].let { userNode ->
				assertSameData(fixtUser, userNode)
				assertThat(userNode.relationships, emptyIterable())
			}
			users[1].let { userNode ->
				assertSameData(expectedNewUser, userNode)
				assertThat(userNode.relationships, emptyIterable())
			}
		}
	}

	// Regression for https://github.com/neo4j/neo4j-ogm/issues/766
	@Test fun `addUser() preserves date format`(graph: GraphDatabaseService) {
		val fixtUserId: String = fixture.build()
		val fixtEmail: String = fixture.build()
		val fixtName: String = fixture.build()
		val fixtRealm: String = fixture.build()
		val fixtCreated: OffsetDateTime = fixture.build<OffsetDateTime>().let {
			it.minusNanos(it.nano % 10000000L)
		}
		assertTrue(
			fixtCreated.toInstant().toEpochMilli() % 10 == 0L,
			"$fixtCreated should have milliseconds ending .??0 to test regression"
		)

		val result = sut.addUser(fixtUserId, fixtEmail, fixtName, fixtRealm, fixtCreated)

		assertThat(result.id, equalTo(fixtUserId))
		assertThat(result.email, equalTo(fixtEmail))
		assertThat(result.name, equalTo(fixtName))
		assertThat(result.realm, equalTo(fixtRealm))
		assertThat(result._created, equalTo(fixtCreated))

		val expectedNewUser = User().apply {
			this.graphId = result.graphId
			this.id = fixtUserId
			this.email = fixtEmail
			this.name = fixtName
			this.realm = fixtRealm
			this._created = fixtCreated
		}
		graph.beginTx().use { tx ->
			val users = tx.allNodes.toList()

			assertThat(users, hasSize(1))
			users.single().let { user ->
				assertSameData(expectedNewUser, user)
				assertThat(user.relationships, emptyIterable())
			}
		}
	}

	@Test fun `addUser() does create a new different user with exact same properties`(
		session: Session, graph: GraphDatabaseService
	) {
		val fixtUser: User = fixture.build()
		session.save(fixtUser)
		val fixtDifferentId: String = fixture.build()

		val result = sut.addUser(fixtDifferentId, fixtUser.email, fixtUser.name, fixtUser.realm, fixtUser._created)

		assertThat(result.id, not(equalTo(fixtUser.id)))
		assertThat(result.email, equalTo(fixtUser.email))
		assertThat(result.name, equalTo(fixtUser.name))
		assertThat(result.realm, equalTo(fixtUser.realm))
		assertThat(result._created, equalTo(fixtUser._created))
		assertThat(result.graphId, not(equalTo(fixtUser.graphId)))

		graph.beginTx().use { tx ->
			val users = tx.allNodes.toList()

			assertThat(users, hasSize(2))
			users[0].let { userNode ->
				assertSameData(fixtUser, userNode)
				assertThat(userNode.relationships, emptyIterable())
			}
			users[1].let { userNode ->
				assertSameData(result, userNode)
				assertThat(userNode.relationships, emptyIterable())
			}
		}
	}

	@Test fun `addUser() updates all fields on change`(session: Session, graph: GraphDatabaseService) {
		val fixtUserId: String = fixture.build()
		val fixtEmail: String = fixture.build()
		val fixtName: String = fixture.build()
		val fixtRealm: String = fixture.build()
		val fixtCreated: OffsetDateTime = fixture.build()
		val savedUser = User().apply {
			this.id = fixtUserId
			this.email = fixtEmail
			this.name = fixtName
			this.realm = fixtRealm
			this._created = fixtCreated
		}
		session.save(savedUser)

		val result = sut.addUser(fixtUserId, fixtEmail, fixtName, fixtRealm, fixtCreated)

		assertThat(result.id, equalTo(fixtUserId))
		assertThat(result.email, equalTo(fixtEmail))
		assertThat(result.name, equalTo(fixtName))
		assertThat(result.realm, equalTo(fixtRealm))
		assertThat(result._created, equalTo(fixtCreated))
		assertThat(result.graphId, equalTo(savedUser.graphId))

		graph.beginTx().use { tx ->
			val users = tx.allNodes.toList()

			assertThat(users, hasSize(1))
			users.single().let { userNode ->
				assertSameData(savedUser, userNode)
				assertThat(userNode.relationships, emptyIterable())
			}
		}
	}
}
