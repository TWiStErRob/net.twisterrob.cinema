package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert.assertNotNull
import org.junit.Test

class FeedTest {

	private fun loadFeed(fileName: String) =
		feedReader().readValue<Feed>(FeedTest::class.java.getResourceAsStream("/$fileName"))

	@Test fun `read UK weekly film times XML`() {
		val feed = loadFeed("weekly_film_times.xml")

		assertNotNull(feed)
		feed.films
	}

	@Test fun `read IE weekly film times XML`() {
		val feed = loadFeed("weekly_film_times_ie.xml")

		assertNotNull(feed)
	}
}
