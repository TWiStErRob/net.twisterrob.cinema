package net.twisterrob.cinema.cineworld.sync.syndication

import io.ktor.client.HttpClient
import net.twisterrob.test.TagExternal
import net.twisterrob.test.TagIntegration
import org.junit.jupiter.api.Test

@TagIntegration
@TagExternal
class FeedServiceNetworkIntgExtTest {

	private val sut = FeedServiceNetwork(HttpClient())

	@Test fun `read weekly film times XML`() {
		val feed = sut.getWeeklyFilmTimes()

		feed.sanityCheck()
	}
}
