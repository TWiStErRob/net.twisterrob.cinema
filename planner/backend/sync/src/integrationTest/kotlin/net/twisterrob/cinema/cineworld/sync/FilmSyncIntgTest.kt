package net.twisterrob.cinema.cineworld.sync

import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.services.Services
import net.twisterrob.test.neo4j.allNodes
import net.twisterrob.test.neo4j.boltURI
import net.twisterrob.test.neo4j.neo4jContainer
import net.twisterrob.test.neo4j.session
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class FilmSyncIntgTest {

	@Container
	private val neo4j = neo4jContainer()

	private lateinit var sut: FilmSync

	@BeforeEach fun setUp() {
		val dagger = DaggerFilmSyncIntgTestComponent
			.builder()
			.graphDBUri(neo4j.boltURI)
			.build()

		sut = dagger.sync
	}

	@Test fun `no cinemas in feed result in no data synced`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())

		sut.sync(feed)

		neo4j.session {
			val allNodes = this.allNodes.toList()
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
