package net.twisterrob.cinema.cineworld.sync.syndication

import io.ktor.client.HttpClient
import org.junit.jupiter.api.Test

class FeedServiceNetworkIntgExtTest {

	private val sut = FeedServiceNetwork(HttpClient())

	@Test fun `read weekly film times XML`() {
		val feed = sut.getWeeklyFilmTimes()

		feed.sanityCheck()
	}
}
