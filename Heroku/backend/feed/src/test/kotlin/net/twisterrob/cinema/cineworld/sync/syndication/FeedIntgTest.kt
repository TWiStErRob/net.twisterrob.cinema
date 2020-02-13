package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.module.kotlin.readValue
import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.GENRES
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.SCREENING_TYPES
import net.twisterrob.test.build
import net.twisterrob.test.get
import net.twisterrob.test.set
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class FeedIntgTest {

	private fun loadFeed(fileName: String): Feed =
		feedReader().readValue(FeedIntgTest::class.java.getResourceAsStream("/$fileName"))

	@Test fun `read UK weekly film times XML`() {
		val feed = loadFeed("weekly_film_times.xml")

		assertNotNull(feed)
		feed.sanityCheck()
		feed.verifyHasAllAttributes(SCREENING_TYPES + GENRES)
	}

	@Test fun `read IE weekly film times XML`() {
		val feed = loadFeed("weekly_film_times_ie.xml")

		assertNotNull(feed)
		feed.sanityCheck()
		feed.verifyHasAllAttributes(SCREENING_TYPES)
	}

	@Disabled("Too much to ask")
	@Test fun `serialization is reversible`() {
		val fixture = JFixture()
		val fixtFeed = fixture.build<Feed>().apply {
			cinemas.forEach { it["performances"] = emptyList<Feed.Screening>() }
			films.forEach { it["performances"] = emptyList<Feed.Screening>() }
			this["performances"] = (1..3).map {
				val performance = Feed.Screening(
					film = films.random(),
					cinema = cinemas.random(),
					url = fixture.build(),
					attributes = SCREENING_TYPES.random().code,
					time = fixture.build()
				)
				performance.film.add("performances", performance)
				performance.cinema.add("performances", performance)
				return@map performance
			}
		}
		val serialized = feedReader().writeValueAsString(fixtFeed)
		println(serialized)
		val feed = feedReader().readValue<Feed>(serialized)
		assertEquals(fixtFeed, feed)
	}

	private fun <T> Any.add(fieldName: String, value: T) {
		val current: Iterable<T> = this[fieldName]
		this[fieldName] = current + value
	}
}
