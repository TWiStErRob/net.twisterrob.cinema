package net.twisterrob.cinema.cineworld.sync

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dagger.Component
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.cineworld.sync.syndication.FeedService
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.services.Services
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.set
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.harness.ServerControls
import org.neo4j.harness.TestServerBuilders
import kotlin.streams.toList

@TagIntegration
class CinemaSyncIntgTest {

	private lateinit var testServer: ServerControls

	private val feedService: FeedService = mock()

	private lateinit var sut: CinemaSync

	@BeforeEach fun setUp() {
		testServer = TestServerBuilders.newInProcessBuilder().newServer()

		val dagger = DaggerCinemaSyncIntgTestComponent
			.builder()
			.graphDBUri(testServer.boltURI())
			.build()

		sut = dagger.sync
		sut["feedService"] = feedService
	}

	@AfterEach fun tearDown() {
		testServer.graph().shutdown()
	}

	@Test fun `no cinemas in feed result in no data synced`() {
		whenever(feedService.getWeeklyFilmTimes())
			.thenReturn(Feed(emptyList(), emptyList(), emptyList(), emptyList()))

		sut.sync()

		testServer.graph().beginTx().apply {
			val allNodes = testServer.graph().allNodes.stream().toList()
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
