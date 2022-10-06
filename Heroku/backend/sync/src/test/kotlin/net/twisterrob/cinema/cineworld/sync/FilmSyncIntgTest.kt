package net.twisterrob.cinema.cineworld.sync

import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.services.Services
import net.twisterrob.test.TagIntegration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import kotlin.streams.toList

@TagIntegration
class FilmSyncIntgTest {

	private lateinit var testServer: Neo4j

	private lateinit var sut: FilmSync

	@BeforeEach fun setUp() {
		testServer = Neo4jBuilders.newInProcessBuilder().build()

		val dagger = DaggerFilmSyncIntgTestComponent
			.builder()
			.graphDBUri(testServer.boltURI())
			.build()

		sut = dagger.sync
	}

	@AfterEach fun tearDown() {
		testServer.close()
	}

	@Test fun `no cinemas in feed result in no data synced`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())

		sut.sync(feed)

		testServer.defaultDatabaseService().beginTx().apply {
			val allNodes = this.allNodes.stream().toList()
			assertThat(allNodes, empty())
			// no relationships either, since there are no nodes to connect
		}
	}
}

@Component(modules = [Neo4JModule::class, SyncAppModule::class])
@Neo4J
private interface FilmSyncIntgTestComponent : Services {

	val sync: FilmSync

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		fun build(): FilmSyncIntgTestComponent
	}
}
