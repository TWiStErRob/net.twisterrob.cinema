package net.twisterrob.cinema.cineworld.sync

import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.services.Services
import net.twisterrob.test.neo4j.allNodes
import net.twisterrob.test.neo4j.boltURI
import net.twisterrob.test.neo4j.session
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders

class CinemaSyncIntgTest {

	private lateinit var neo4j: Neo4j

	private lateinit var sut: CinemaSync

	@BeforeEach fun setUp() {
		neo4j = Neo4jBuilders.newInProcessBuilder().build()

		val dagger = DaggerCinemaSyncIntgTestComponent
			.builder()
			.graphDBUri(neo4j.boltURI)
			.build()

		sut = dagger.sync
	}

	@AfterEach fun tearDown() {
		neo4j.close()
	}

	@Test fun `no cinemas in feed result in no data synced`() {
		val emptyFeed = Feed(emptyList(), emptyList(), emptyList(), emptyList())

		sut.sync(emptyFeed)

		neo4j.session {
			val allNodes = this.allNodes.toList()
			assertThat(allNodes, empty())
			// no relationships either, since there are no nodes to connect
		}
	}
}

@Component(modules = [Neo4JModule::class, SyncAppModule::class])
@Neo4J
private interface CinemaSyncIntgTestComponent : Services {

	val sync: CinemaSync

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): CinemaSyncIntgTestComponent
	}
}
